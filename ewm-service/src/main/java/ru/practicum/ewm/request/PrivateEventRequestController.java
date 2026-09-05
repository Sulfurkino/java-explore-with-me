package ru.practicum.ewm.request;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events/{eventId}/requests")
public class PrivateEventRequestController {

	private final RequestService requestService;

	@GetMapping
	public List<ParticipationRequestDto> getEventParticipants(
			@PathVariable Long userId,
			@PathVariable Long eventId) {
		return requestService.getEventRequests(userId, eventId);
	}

	@PatchMapping
	public EventRequestStatusUpdateResult changeRequestStatus(
			@PathVariable Long userId,
			@PathVariable Long eventId,
			@RequestBody @Valid EventRequestStatusUpdateRequest dto) {
		return requestService.updateStatus(userId, eventId, dto);
	}
}
