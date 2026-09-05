package ru.practicum.ewm.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventState;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private UserService userService;

	@Mock
	private EventService eventService;

	@InjectMocks
	private RequestServiceImpl requestService;

	@Test
	void initiatorCannotRequestOwnEvent() {
		User user = user(1L);
		Event event = event(user, EventState.PUBLISHED, 10, true);
		when(userService.getById(1L)).thenReturn(user);
		when(eventService.getEvent(5L)).thenReturn(event);

		assertThatThrownBy(() -> requestService.addRequest(1L, 5L))
				.isInstanceOf(ConflictException.class);
	}

	@Test
	void unpublishedEventCannotBeRequested() {
		User user = user(2L);
		Event event = event(user(1L), EventState.PENDING, 10, true);
		when(userService.getById(2L)).thenReturn(user);
		when(eventService.getEvent(5L)).thenReturn(event);

		assertThatThrownBy(() -> requestService.addRequest(2L, 5L))
				.isInstanceOf(ConflictException.class);
	}

	@Test
	void autoConfirmsWhenModerationDisabled() {
		User user = user(2L);
		Event event = event(user(1L), EventState.PUBLISHED, 10, false);
		when(userService.getById(2L)).thenReturn(user);
		when(eventService.getEvent(5L)).thenReturn(event);
		when(requestRepository.existsByEventIdAndRequesterId(5L, 2L)).thenReturn(false);
		when(requestRepository.countByEventIdAndStatus(5L, RequestStatus.CONFIRMED)).thenReturn(0L);
		when(requestRepository.save(any(ParticipationRequest.class))).thenAnswer(invocation -> {
			ParticipationRequest request = invocation.getArgument(0);
			request.setId(9L);
			return request;
		});

		ParticipationRequestDto dto = requestService.addRequest(2L, 5L);

		assertThat(dto.getStatus()).isEqualTo(RequestStatus.CONFIRMED);
	}

	@Test
	void rejectsWhenLimitReached() {
		User user = user(2L);
		Event event = event(user(1L), EventState.PUBLISHED, 1, true);
		when(userService.getById(2L)).thenReturn(user);
		when(eventService.getEvent(5L)).thenReturn(event);
		when(requestRepository.existsByEventIdAndRequesterId(5L, 2L)).thenReturn(false);
		when(requestRepository.countByEventIdAndStatus(5L, RequestStatus.CONFIRMED)).thenReturn(1L);

		assertThatThrownBy(() -> requestService.addRequest(2L, 5L))
				.isInstanceOf(ConflictException.class)
				.hasMessage("The participant limit has been reached");
	}

	private Event event(User initiator, EventState state, int limit, boolean moderation) {
		Event event = new Event();
		event.setId(5L);
		event.setInitiator(initiator);
		event.setState(state);
		event.setParticipantLimit(limit);
		event.setRequestModeration(moderation);
		event.setEventDate(LocalDateTime.now().plusDays(2));
		return event;
	}

	private User user(Long id) {
		User user = new User();
		user.setId(id);
		user.setName("User");
		user.setEmail("user" + id + "@yandex.ru");
		return user;
	}
}
