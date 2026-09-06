package ru.practicum.ewm.comment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

	@Mock
	private CommentRepository commentRepository;

	@Mock
	private UserService userService;

	@Mock
	private EventService eventService;

	@InjectMocks
	private CommentServiceImpl commentService;

	@Test
	void addCreatesCommentForPublishedEvent() {
		User author = user(2L);
		Event event = event(EventState.PUBLISHED, user(1L));
		when(userService.getById(2L)).thenReturn(author);
		when(eventService.getEvent(5L)).thenReturn(event);
		when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
			Comment comment = invocation.getArgument(0);
			comment.setId(9L);
			return comment;
		});
		NewCommentDto dto = newComment("Great event, I will come");

		CommentDto result = commentService.add(2L, dto);

		assertThat(result.getId()).isEqualTo(9L);
		assertThat(result.getText()).isEqualTo("Great event, I will come");
		assertThat(result.getEventId()).isEqualTo(5L);
		assertThat(result.getAuthor().getId()).isEqualTo(2L);
	}

	@Test
	void addRejectsUnpublishedEvent() {
		when(userService.getById(2L)).thenReturn(user(2L));
		when(eventService.getEvent(5L)).thenReturn(event(EventState.PENDING, user(1L)));

		assertThatThrownBy(() -> commentService.add(2L, newComment("Great event, I will come")))
				.isInstanceOf(ConflictException.class)
				.hasMessage("Cannot comment on an unpublished event");
	}

	@Test
	void updateChangesTextWhenAuthor() {
		Comment comment = comment(9L, user(2L), event(EventState.PUBLISHED, user(1L)));
		when(userService.getById(2L)).thenReturn(user(2L));
		when(commentRepository.findByIdWithRelations(9L)).thenReturn(Optional.of(comment));
		when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));
		UpdateCommentRequest dto = new UpdateCommentRequest();
		dto.setText("Updated comment text here");

		CommentDto result = commentService.update(2L, 9L, dto);

		assertThat(result.getText()).isEqualTo("Updated comment text here");
		assertThat(result.getUpdated()).isNotNull();
	}

	@Test
	void updateForbiddenForNonAuthor() {
		Comment comment = comment(9L, user(2L), event(EventState.PUBLISHED, user(1L)));
		when(userService.getById(3L)).thenReturn(user(3L));
		when(commentRepository.findByIdWithRelations(9L)).thenReturn(Optional.of(comment));
		UpdateCommentRequest dto = new UpdateCommentRequest();
		dto.setText("Updated comment text here");

		assertThatThrownBy(() -> commentService.update(3L, 9L, dto))
				.isInstanceOf(ForbiddenException.class)
				.hasMessage("User is not the author of the comment");
	}

	@Test
	void getByEventHidesUnpublishedEvent() {
		when(eventService.getEvent(5L)).thenReturn(event(EventState.PENDING, user(1L)));

		assertThatThrownBy(() -> commentService.getByEvent(5L, 0, 10))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void getPublishedHidesCommentOfUnpublishedEvent() {
		Comment comment = comment(9L, user(2L), event(EventState.PENDING, user(1L)));
		when(commentRepository.findByIdWithRelations(9L)).thenReturn(Optional.of(comment));

		assertThatThrownBy(() -> commentService.getPublished(9L))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void deleteByAuthorRemovesOwnComment() {
		Comment comment = comment(9L, user(2L), event(EventState.PUBLISHED, user(1L)));
		when(userService.getById(2L)).thenReturn(user(2L));
		when(commentRepository.findByIdWithRelations(9L)).thenReturn(Optional.of(comment));

		commentService.deleteByAuthor(2L, 9L);

		verify(commentRepository).delete(comment);
	}

	@Test
	void deleteByAdminRemovesAnyComment() {
		Comment comment = comment(9L, user(2L), event(EventState.PUBLISHED, user(1L)));
		when(commentRepository.findByIdWithRelations(9L)).thenReturn(Optional.of(comment));

		commentService.deleteByAdmin(9L);

		verify(commentRepository).delete(comment);
	}

	@Test
	void deleteByAdminNotFound() {
		when(commentRepository.findByIdWithRelations(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> commentService.deleteByAdmin(99L))
				.isInstanceOf(NotFoundException.class)
				.hasMessage("Comment with id=99 was not found");
	}

	private NewCommentDto newComment(String text) {
		NewCommentDto dto = new NewCommentDto();
		dto.setEventId(5L);
		dto.setText(text);
		return dto;
	}

	private Comment comment(Long id, User author, Event event) {
		Comment comment = new Comment();
		comment.setId(id);
		comment.setText("Great event, I will come");
		comment.setAuthor(author);
		comment.setEvent(event);
		comment.setCreated(LocalDateTime.now().minusHours(1));
		return comment;
	}

	private Event event(EventState state, User initiator) {
		Event event = new Event();
		event.setId(5L);
		event.setState(state);
		event.setInitiator(initiator);
		event.setEventDate(LocalDateTime.now().plusDays(2));
		return event;
	}

	private User user(Long id) {
		User user = new User();
		user.setId(id);
		user.setName("User" + id);
		user.setEmail("user" + id + "@yandex.ru");
		return user;
	}
}
