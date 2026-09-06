package ru.practicum.ewm.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.stats.EventStatsService;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

	@Mock
	private EventRepository eventRepository;

	@Mock
	private RequestRepository requestRepository;

	@Mock
	private UserService userService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private EventStatsService eventStatsService;

	@InjectMocks
	private EventServiceImpl eventService;

	@Test
	void addEventRejectsDateSoonerThanTwoHours() {
		when(userService.getById(1L)).thenReturn(user(1L));
		when(categoryService.getById(2L)).thenReturn(category());
		NewEventDto dto = newEventDto(LocalDateTime.now().plusMinutes(30));

		assertThatThrownBy(() -> eventService.addEvent(1L, dto))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	void getPublicEventHidesUnpublished() {
		Event event = event(EventState.PENDING);
		when(eventRepository.findByIdWithRelations(5L)).thenReturn(Optional.of(event));

		assertThatThrownBy(() -> eventService.getPublicEvent(5L, "127.0.0.1"))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void getPublicEventRecordsHitAndReturnsViews() {
		Event event = event(EventState.PUBLISHED);
		when(eventRepository.findByIdWithRelations(5L)).thenReturn(Optional.of(event));
		when(eventStatsService.getViews(List.of(5L))).thenReturn(Map.of(5L, 9L));

		EventFullDto dto = eventService.getPublicEvent(5L, "10.0.0.1");

		assertThat(dto.getViews()).isEqualTo(9L);
		verify(eventStatsService).hit(eq("/events/5"), eq("10.0.0.1"));
	}

	@Test
	void updateUserEventRejectsPublished() {
		when(userService.getById(1L)).thenReturn(user(1L));
		when(eventRepository.findByIdAndInitiatorId(5L, 1L)).thenReturn(Optional.of(event(EventState.PUBLISHED)));

		assertThatThrownBy(() -> eventService.updateUserEvent(1L, 5L, new UpdateEventUserRequest()))
				.isInstanceOf(ForbiddenException.class)
				.hasMessage("Only pending or canceled events can be changed");
	}

	@Test
	void publishRequiresPendingState() {
		Event event = event(EventState.PUBLISHED);
		when(eventRepository.findByIdWithRelations(5L)).thenReturn(Optional.of(event));
		UpdateEventAdminRequest dto = new UpdateEventAdminRequest();
		dto.setStateAction(AdminStateAction.PUBLISH_EVENT);

		assertThatThrownBy(() -> eventService.updateAdminEvent(5L, dto))
				.isInstanceOf(ForbiddenException.class)
				.hasMessageContaining("Cannot publish the event");
	}

	@Test
	void getPublicEventsSortsByViewsThenLoadsOnlyRequestedPage() {
		Event high = event(EventState.PUBLISHED);
		high.setId(2L);
		Event mid = event(EventState.PUBLISHED);
		mid.setId(3L);
		when(eventRepository.findIds(any())).thenReturn(List.of(1L, 2L, 3L));
		when(eventStatsService.getViews(any())).thenReturn(Map.of(1L, 1L, 2L, 10L, 3L, 5L));
		when(eventRepository.findAllWithRelationsByIdIn(List.of(2L, 3L))).thenReturn(List.of(mid, high));
		when(requestRepository.countByEventIdInAndStatus(any(), any())).thenReturn(List.of());

		List<EventShortDto> result = eventService.getPublicEvents(
				null, null, null, null, null, false, EventSort.VIEWS, 0, 2, "127.0.0.1");

		assertThat(result).extracting(EventShortDto::getId).containsExactly(2L, 3L);
		verify(eventRepository).findAllWithRelationsByIdIn(List.of(2L, 3L));
		verify(eventStatsService).hit("/events", "127.0.0.1");
	}

	private NewEventDto newEventDto(LocalDateTime eventDate) {
		NewEventDto dto = new NewEventDto();
		dto.setAnnotation("annotation annotation");
		dto.setCategory(2L);
		dto.setDescription("description description");
		dto.setEventDate(eventDate);
		dto.setLocation(new Location(55.7f, 37.6f));
		dto.setTitle("title");
		return dto;
	}

	private Event event(EventState state) {
		Event event = new Event();
		event.setId(5L);
		event.setAnnotation("annotation annotation");
		event.setCategory(category());
		event.setCreatedOn(LocalDateTime.now().minusDays(1));
		event.setDescription("description description");
		event.setEventDate(LocalDateTime.now().plusDays(2));
		event.setInitiator(user(1L));
		event.setLocation(new Location(1f, 2f));
		event.setPaid(false);
		event.setParticipantLimit(0);
		event.setRequestModeration(true);
		event.setState(state);
		event.setTitle("title");
		return event;
	}

	private User user(Long id) {
		User user = new User();
		user.setId(id);
		user.setName("Ivan");
		user.setEmail("ivan@yandex.ru");
		return user;
	}

	private Category category() {
		Category category = new Category();
		category.setId(2L);
		category.setName("Concerts");
		return category;
	}
}
