package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewCompilationDto {

	private Set<Long> events;

	private Boolean pinned = false;

	@NotBlank
	@Size(min = 1, max = 50)
	private String title;
}
