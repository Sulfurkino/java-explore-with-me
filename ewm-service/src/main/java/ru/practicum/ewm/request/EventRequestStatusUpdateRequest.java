package ru.practicum.ewm.request;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventRequestStatusUpdateRequest {

	@NotNull
	private List<Long> requestIds;

	@NotNull
	private RequestStatus status;
}
