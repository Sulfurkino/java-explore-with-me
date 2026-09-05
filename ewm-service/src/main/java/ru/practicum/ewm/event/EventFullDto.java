package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.UserShortDto;

@Getter
@Setter
public class EventFullDto {

	private String annotation;
	private CategoryDto category;
	private Long confirmedRequests;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdOn;

	private String description;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime eventDate;

	private Long id;
	private UserShortDto initiator;
	private Location location;
	private Boolean paid;
	private Integer participantLimit;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime publishedOn;

	private Boolean requestModeration;
	private EventState state;
	private String title;
	private Long views;
}
