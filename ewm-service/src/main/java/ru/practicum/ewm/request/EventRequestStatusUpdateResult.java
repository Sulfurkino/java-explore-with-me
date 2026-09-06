package ru.practicum.ewm.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateResult {

	private List<ParticipationRequestDto> confirmedRequests;
	private List<ParticipationRequestDto> rejectedRequests;
}
