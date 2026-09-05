package ru.practicum.ewm.request;

import java.util.List;

public interface RequestService {

	ParticipationRequestDto addRequest(Long userId, Long eventId);

	List<ParticipationRequestDto> getUserRequests(Long userId);

	ParticipationRequestDto cancelRequest(Long userId, Long requestId);

	List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId);

	EventRequestStatusUpdateResult updateStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);
}
