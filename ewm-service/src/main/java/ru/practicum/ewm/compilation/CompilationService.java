package ru.practicum.ewm.compilation;

import java.util.List;

public interface CompilationService {

	CompilationDto create(NewCompilationDto dto);

	void delete(Long compId);

	CompilationDto update(Long compId, UpdateCompilationRequest dto);

	List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

	CompilationDto getCompilation(Long compId);
}
