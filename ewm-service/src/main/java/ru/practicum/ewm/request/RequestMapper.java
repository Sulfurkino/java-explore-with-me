package ru.practicum.ewm.request;

public final class RequestMapper {

	private RequestMapper() {
	}

	public static ParticipationRequestDto toDto(ParticipationRequest request) {
		return new ParticipationRequestDto(
				request.getCreated(),
				request.getEvent().getId(),
				request.getId(),
				request.getRequester().getId(),
				request.getStatus());
	}
}
