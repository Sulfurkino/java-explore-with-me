package ru.practicum.ewm.event;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventService {

	EventFullDto addEvent(Long userId, NewEventDto dto);

	List<EventShortDto> getUserEvents(Long userId, int from, int size);

	EventFullDto getUserEvent(Long userId, Long eventId);

	EventFullDto updateUserEvent(Long userId, Long eventId, UpdateEventUserRequest dto);

	List<EventFullDto> getAdminEvents(List<Long> users, List<EventState> states, List<Long> categories,
			LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

	EventFullDto updateAdminEvent(Long eventId, UpdateEventAdminRequest dto);

	List<EventShortDto> getPublicEvents(String text, List<Long> categories, Boolean paid,
			LocalDateTime rangeStart, LocalDateTime rangeEnd, boolean onlyAvailable, EventSort sort,
			int from, int size, String ip);

	EventFullDto getPublicEvent(Long eventId, String ip);

	Event getEvent(Long eventId);

	List<Event> getEvents(Collection<Long> ids);

	List<EventShortDto> toShortDtos(List<Event> events);
}
