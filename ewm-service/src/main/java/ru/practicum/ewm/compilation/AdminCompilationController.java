package ru.practicum.ewm.compilation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

	private final CompilationService compilationService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CompilationDto saveCompilation(@RequestBody @Valid NewCompilationDto dto) {
		return compilationService.create(dto);
	}

	@DeleteMapping("/{compId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCompilation(@PathVariable Long compId) {
		compilationService.delete(compId);
	}

	@PatchMapping("/{compId}")
	public CompilationDto updateCompilation(
			@PathVariable Long compId,
			@RequestBody @Valid UpdateCompilationRequest dto) {
		return compilationService.update(compId, dto);
	}
}
