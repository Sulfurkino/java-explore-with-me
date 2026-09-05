package ru.practicum.ewm.compilation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.event.EventShortDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

	private List<EventShortDto> events;
	private Long id;
	private Boolean pinned;
	private String title;
}
