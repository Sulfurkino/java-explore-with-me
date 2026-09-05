package ru.practicum.ewm.event;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/events")
public class AdminEventController {

	private final EventService eventService;

	@GetMapping
	public List<EventFullDto> getEvents(
			@RequestParam(required = false) List<Long> users,
			@RequestParam(required = false) List<EventState> states,
			@RequestParam(required = false) List<Long> categories,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size) {
		return eventService.getAdminEvents(users, states, categories, rangeStart, rangeEnd, from, size);
	}

	@PatchMapping("/{eventId}")
	public EventFullDto updateEvent(
			@PathVariable Long eventId,
			@RequestBody @Valid UpdateEventAdminRequest dto) {
		return eventService.updateAdminEvent(eventId, dto);
	}
}
