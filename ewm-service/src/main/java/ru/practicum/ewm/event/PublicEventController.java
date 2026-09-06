package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class PublicEventController {

	private final EventService eventService;

	@GetMapping
	public List<EventShortDto> getEvents(
			@RequestParam(required = false) String text,
			@RequestParam(required = false) List<Long> categories,
			@RequestParam(required = false) Boolean paid,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
			@RequestParam(defaultValue = "false") boolean onlyAvailable,
			@RequestParam(required = false) EventSort sort,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size,
			HttpServletRequest request) {
		return eventService.getPublicEvents(
				text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request.getRemoteAddr());
	}

	@GetMapping("/{id}")
	public EventFullDto getEvent(@PathVariable Long id, HttpServletRequest request) {
		return eventService.getPublicEvent(id, request.getRemoteAddr());
	}
}
