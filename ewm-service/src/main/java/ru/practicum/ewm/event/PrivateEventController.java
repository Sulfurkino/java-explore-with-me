package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

	private final EventService eventService;

	@GetMapping
	public List<EventShortDto> getEvents(
			@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size) {
		return eventService.getUserEvents(userId, from, size);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto dto) {
		return eventService.addEvent(userId, dto);
	}

	@GetMapping("/{eventId}")
	public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
		return eventService.getUserEvent(userId, eventId);
	}

	@PatchMapping("/{eventId}")
	public EventFullDto updateEvent(
			@PathVariable Long userId,
			@PathVariable Long eventId,
			@RequestBody @Valid UpdateEventUserRequest dto) {
		return eventService.updateUserEvent(userId, eventId, dto);
	}
}
