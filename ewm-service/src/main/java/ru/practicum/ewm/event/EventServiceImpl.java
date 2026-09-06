package ru.practicum.ewm.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.OffsetPageRequest;
import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryService;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.RequestRepository;
import ru.practicum.ewm.request.RequestStatus;
import ru.practicum.ewm.stats.EventStatsService;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

	private final EventRepository eventRepository;
	private final RequestRepository requestRepository;
	private final UserService userService;
	private final CategoryService categoryService;
	private final EventStatsService eventStatsService;

	@Override
	@Transactional
	public EventFullDto addEvent(Long userId, NewEventDto dto) {
		User user = userService.getById(userId);
		Category category = categoryService.getById(dto.getCategory());
		validateEventDate(dto.getEventDate(), 2);
		Event event = EventMapper.toEvent(dto, category, user);
		event.setCreatedOn(LocalDateTime.now());
		return toFullDto(eventRepository.save(event));
	}

	@Override
	public List<EventShortDto> getUserEvents(Long userId, int from, int size) {
		userService.getById(userId);
		List<Event> events = eventRepository.findAllByInitiatorId(userId, new OffsetPageRequest(from, size));
		return toShortDtos(events);
	}

	@Override
	public EventFullDto getUserEvent(Long userId, Long eventId) {
		userService.getById(userId);
		Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
				.orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
		return toFullDto(event);
	}

	@Override
	@Transactional
	public EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto) {
		userService.getById(userId);
		Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
				.orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
		if (event.getState() == EventState.PUBLISHED) {
			throw new ForbiddenException("Only pending or canceled events can be changed");
		}
		if (dto.getEventDate() != null) {
			validateEventDate(dto.getEventDate(), 2);
		}
		Category category = dto.getCategory() == null ? null : categoryService.getById(dto.getCategory());
		apply(event, dto.getAnnotation(), category, dto.getDescription(), dto.getEventDate(),
				dto.getLocation(), dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration(),
				dto.getTitle());
		if (dto.getStateAction() == UserStateAction.SEND_TO_REVIEW) {
			event.setState(EventState.PENDING);
		} else if (dto.getStateAction() == UserStateAction.CANCEL_REVIEW) {
			event.setState(EventState.CANCELED);
		}
		return toFullDto(eventRepository.save(event));
	}

	@Override
	public List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
			LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
		validateRange(rangeStart, rangeEnd);
		Page<Event> page = eventRepository.findAll(
				EventSpecifications.adminSearch(users, states, categories, rangeStart, rangeEnd),
				new OffsetPageRequest(from, size, Sort.by("id").ascending()));
		return toFullDtos(page.getContent());
	}

	@Override
	@Transactional
	public EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest dto) {
		Event event = getEvent(eventId);
		if (dto.getEventDate() != null) {
			validateEventDate(dto.getEventDate(), 1);
		}
		if (dto.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
			if (event.getState() != EventState.PENDING) {
				throw new ForbiddenException("Cannot publish the event because it's not in the right state: "
						+ event.getState());
			}
			LocalDateTime eventDate = dto.getEventDate() == null ? event.getEventDate() : dto.getEventDate();
			validateEventDate(eventDate, 1);
			event.setState(EventState.PUBLISHED);
			event.setPublishedOn(LocalDateTime.now());
		} else if (dto.getStateAction() == AdminStateAction.REJECT_EVENT) {
			if (event.getState() == EventState.PUBLISHED) {
				throw new ForbiddenException("Cannot reject the event because it's not in the right state: "
						+ event.getState());
			}
			event.setState(EventState.CANCELED);
		}
		Category category = dto.getCategory() == null ? null : categoryService.getById(dto.getCategory());
		apply(event, dto.getAnnotation(), category, dto.getDescription(), dto.getEventDate(),
				dto.getLocation(), dto.getPaid(), dto.getParticipantLimit(), dto.getRequestModeration(),
				dto.getTitle());
		return toFullDto(eventRepository.save(event));
	}

	@Override
	public List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
			LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort,
			int from, int size, String ip) {
		validateRange(rangeStart, rangeEnd);
		LocalDateTime start = rangeStart;
		LocalDateTime end = rangeEnd;
		if (start == null && end == null) {
			start = LocalDateTime.now();
		}
		Specification<Event> spec = EventSpecifications.publicSearch(
				text, categories, paid, start, end, onlyAvailable);
		eventStatsService.hit("/events", ip);
		if (sort == EventSort.VIEWS) {
			return getPublicEventsByViews(spec, from, size);
		}
		Page<Event> page = eventRepository.findAll(spec,
				new OffsetPageRequest(from, size, Sort.by("eventDate").ascending()));
		return toShortDtos(page.getContent());
	}

	@Override
	public EventFullDto getPublicEvent(Long eventId, String ip) {
		Event event = getEvent(eventId);
		if (event.getState() != EventState.PUBLISHED) {
			throw new NotFoundException("Event with id=" + eventId + " was not found");
		}
		eventStatsService.hit("/events/" + eventId, ip);
		return toFullDto(event);
	}

	@Override
	public Event getEvent(Long eventId) {
		return eventRepository.findByIdWithRelations(eventId)
				.orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
	}

	@Override
	public List<Event> getEvents(Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		return eventRepository.findAllWithRelationsByIdIn(ids);
	}

	private void apply(Event event, String annotation, Category category, String description,
			LocalDateTime eventDate, Location location, Boolean paid, Integer participantLimit,
			Boolean requestModeration, String title) {
		EventMapper.update(event, annotation, null, description, eventDate, location, paid, participantLimit,
				requestModeration, title, category);
	}

	private void validateEventDate(LocalDateTime eventDate, int hours) {
		if (eventDate.isBefore(LocalDateTime.now().plusHours(hours))) {
			throw new IllegalArgumentException(
					"Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: " + eventDate);
		}
	}

	private void validateRange(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
		if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
			throw new IllegalArgumentException("Range start must not be after end");
		}
	}

	private List<EventShortDto> getPublicEventsByViews(Specification<Event> spec, int from, int size) {
		List<Long> ids = new ArrayList<>(eventRepository.findIds(spec));
		if (from >= ids.size()) {
			return List.of();
		}
		Map<Long, Long> views = eventStatsService.getViews(ids);
		ids.sort(Comparator.comparing((Long id) -> views.getOrDefault(id, 0L)).reversed()
				.thenComparingLong(id -> id));
		List<Long> pageIds = ids.subList(from, Math.min(from + size, ids.size()));
		Map<Long, Event> byId = eventRepository.findAllWithRelationsByIdIn(pageIds).stream()
				.collect(Collectors.toMap(Event::getId, event -> event));
		List<Event> events = pageIds.stream()
				.map(byId::get)
				.filter(Objects::nonNull)
				.toList();
		return toShortDtos(events, views);
	}

	private EventFullDto toFullDto(Event event) {
		long confirmed = requestRepository.countByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED);
		long views = eventStatsService.getViews(List.of(event.getId())).getOrDefault(event.getId(), 0L);
		return EventMapper.toFullDto(event, confirmed, views);
	}

	private List<EventFullDto> toFullDtos(List<Event> events) {
		Map<Long, Long> confirmed = confirmedCounts(events);
		Map<Long, Long> views = eventStatsService.getViews(events.stream().map(Event::getId).toList());
		return events.stream()
				.map(event -> EventMapper.toFullDto(
						event,
						confirmed.getOrDefault(event.getId(), 0L),
						views.getOrDefault(event.getId(), 0L)))
				.toList();
	}

	@Override
	public List<EventShortDto> toShortDtos(List<Event> events) {
		return toShortDtos(events, eventStatsService.getViews(events.stream().map(Event::getId).toList()));
	}

	private List<EventShortDto> toShortDtos(List<Event> events, Map<Long, Long> views) {
		Map<Long, Long> confirmed = confirmedCounts(events);
		return events.stream()
				.map(event -> EventMapper.toShortDto(
						event,
						confirmed.getOrDefault(event.getId(), 0L),
						views.getOrDefault(event.getId(), 0L)))
				.toList();
	}

	private Map<Long, Long> confirmedCounts(List<Event> events) {
		if (events.isEmpty()) {
			return Map.of();
		}
		Map<Long, Long> counts = new HashMap<>();
		List<Object[]> rows = requestRepository.countByEventIdInAndStatus(
				events.stream().map(Event::getId).toList(), RequestStatus.CONFIRMED);
		for (Object[] row : rows) {
			counts.put((Long) row[0], (Long) row[1]);
		}
		return counts;
	}
}
