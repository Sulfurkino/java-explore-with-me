package ru.practicum.ewm.compilation;

import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCompilationRequest {

	private Set<Long> events;

	private Boolean pinned;

	@Size(min = 1, max = 50)
	private String title;
}
