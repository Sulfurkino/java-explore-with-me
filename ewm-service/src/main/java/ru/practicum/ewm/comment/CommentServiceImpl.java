package ru.practicum.ewm.comment;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.OffsetPageRequest;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

	private static final String CONDITIONS = "For the requested operation the conditions are not met.";

	private final CommentRepository commentRepository;
	private final UserService userService;
	private final EventService eventService;

	@Override
	@Transactional
	public CommentDto add(Long userId, NewCommentDto dto) {
		User author = userService.getById(userId);
		Event event = eventService.getEvent(dto.getEventId());
		if (event.getState() != EventState.PUBLISHED) {
			throw new ConflictException(CONDITIONS, "Cannot comment on an unpublished event");
		}
		Comment comment = new Comment();
		comment.setText(dto.getText());
		comment.setEvent(event);
		comment.setAuthor(author);
		comment.setCreated(LocalDateTime.now());
		return CommentMapper.toDto(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public CommentDto update(Long userId, Long commentId, UpdateCommentRequest dto) {
		userService.getById(userId);
		Comment comment = getComment(commentId);
		requireAuthor(comment, userId);
		comment.setText(dto.getText());
		comment.setUpdated(LocalDateTime.now());
		return CommentMapper.toDto(commentRepository.save(comment));
	}

	@Override
	@Transactional
	public void deleteByAuthor(Long userId, Long commentId) {
		userService.getById(userId);
		Comment comment = getComment(commentId);
		requireAuthor(comment, userId);
		commentRepository.delete(comment);
	}

	@Override
	public List<CommentDto> getByAuthor(Long userId, int from, int size) {
		userService.getById(userId);
		return commentRepository.findAllByAuthorId(userId, new OffsetPageRequest(from, size)).stream()
				.map(CommentMapper::toDto)
				.toList();
	}

	@Override
	public List<CommentDto> getByEvent(Long eventId, int from, int size) {
		requirePublishedEvent(eventId);
		return commentRepository.findAllByEventId(eventId, new OffsetPageRequest(from, size)).stream()
				.map(CommentMapper::toDto)
				.toList();
	}

	@Override
	public CommentDto getPublished(Long commentId) {
		Comment comment = getComment(commentId);
		if (comment.getEvent().getState() != EventState.PUBLISHED) {
			throw new NotFoundException("Comment with id=" + commentId + " was not found");
		}
		return CommentMapper.toDto(comment);
	}

	@Override
	@Transactional
	public void deleteByAdmin(Long commentId) {
		Comment comment = getComment(commentId);
		commentRepository.delete(comment);
	}

	@Override
	public List<CommentDto> getAdminComments(Long eventId, int from, int size) {
		OffsetPageRequest pageable = new OffsetPageRequest(from, size);
		List<Comment> comments = eventId == null
				? commentRepository.findAllWithRelations(pageable)
				: commentRepository.findAllByEventId(eventId, pageable);
		return comments.stream()
				.map(CommentMapper::toDto)
				.toList();
	}

	private Comment getComment(Long commentId) {
		return commentRepository.findByIdWithRelations(commentId)
				.orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
	}

	private void requirePublishedEvent(Long eventId) {
		Event event = eventService.getEvent(eventId);
		if (event.getState() != EventState.PUBLISHED) {
			throw new NotFoundException("Event with id=" + eventId + " was not found");
		}
	}

	private void requireAuthor(Comment comment, Long userId) {
		if (!comment.getAuthor().getId().equals(userId)) {
			throw new ForbiddenException("User is not the author of the comment");
		}
	}
}
