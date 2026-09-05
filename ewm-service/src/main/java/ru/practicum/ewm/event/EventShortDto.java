package ru.practicum.ewm.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.category.CategoryDto;
import ru.practicum.ewm.user.UserShortDto;

@Getter
@Setter
public class EventShortDto {

	private String annotation;
	private CategoryDto category;
	private Long confirmedRequests;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime eventDate;

	private Long id;
	private UserShortDto initiator;
	private Boolean paid;
	private String title;
	private Long views;
}
