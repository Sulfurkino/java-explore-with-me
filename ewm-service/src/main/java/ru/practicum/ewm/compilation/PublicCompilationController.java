package ru.practicum.ewm.compilation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/compilations")
public class PublicCompilationController {

	private final CompilationService compilationService;

	@GetMapping
	public List<CompilationDto> getCompilations(
			@RequestParam(required = false) Boolean pinned,
			@RequestParam(defaultValue = "0") int from,
			@RequestParam(defaultValue = "10") int size) {
		return compilationService.getCompilations(pinned, from, size);
	}

	@GetMapping("/{compId}")
	public CompilationDto getCompilation(@PathVariable Long compId) {
		return compilationService.getCompilation(compId);
	}
}
