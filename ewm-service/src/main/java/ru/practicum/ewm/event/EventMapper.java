package ru.practicum.ewm.event;

import ru.practicum.ewm.category.Category;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.user.User;
import ru.practicum.ewm.user.UserMapper;

public final class EventMapper {

	private EventMapper() {
	}

	public static Event toEvent(NewEventDto dto, Category category, User initiator) {
		Event event = new Event();
		event.setAnnotation(dto.getAnnotation());
		event.setCategory(category);
		event.setDescription(dto.getDescription());
		event.setEventDate(dto.getEventDate());
		event.setInitiator(initiator);
		event.setLocation(dto.getLocation());
		event.setPaid(Boolean.TRUE.equals(dto.getPaid()));
		event.setParticipantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit());
		event.setRequestModeration(dto.getRequestModeration() == null || dto.getRequestModeration());
		event.setState(EventState.PENDING);
		event.setTitle(dto.getTitle());
		return event;
	}

	public static EventShortDto toShortDto(Event event, long confirmedRequests, long views) {
		EventShortDto dto = new EventShortDto();
		dto.setAnnotation(event.getAnnotation());
		dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
		dto.setConfirmedRequests(confirmedRequests);
		dto.setEventDate(event.getEventDate());
		dto.setId(event.getId());
		dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
		dto.setPaid(event.getPaid());
		dto.setTitle(event.getTitle());
		dto.setViews(views);
		return dto;
	}

	public static EventFullDto toFullDto(Event event, long confirmedRequests, long views) {
		EventFullDto dto = new EventFullDto();
		dto.setAnnotation(event.getAnnotation());
		dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
		dto.setConfirmedRequests(confirmedRequests);
		dto.setCreatedOn(event.getCreatedOn());
		dto.setDescription(event.getDescription());
		dto.setEventDate(event.getEventDate());
		dto.setId(event.getId());
		dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
		dto.setLocation(event.getLocation());
		dto.setPaid(event.getPaid());
		dto.setParticipantLimit(event.getParticipantLimit());
		dto.setPublishedOn(event.getPublishedOn());
		dto.setRequestModeration(event.getRequestModeration());
		dto.setState(event.getState());
		dto.setTitle(event.getTitle());
		dto.setViews(views);
		return dto;
	}

	public static void update(Event event, String annotation, Long unusedCategory, String description,
			java.time.LocalDateTime eventDate, Location location, Boolean paid, Integer participantLimit,
			Boolean requestModeration, String title, Category category) {
		if (annotation != null) {
			event.setAnnotation(annotation);
		}
		if (category != null) {
			event.setCategory(category);
		}
		if (description != null) {
			event.setDescription(description);
		}
		if (eventDate != null) {
			event.setEventDate(eventDate);
		}
		if (location != null) {
			event.setLocation(location);
		}
		if (paid != null) {
			event.setPaid(paid);
		}
		if (participantLimit != null) {
			event.setParticipantLimit(participantLimit);
		}
		if (requestModeration != null) {
			event.setRequestModeration(requestModeration);
		}
		if (title != null) {
			event.setTitle(title);
		}
	}
}
