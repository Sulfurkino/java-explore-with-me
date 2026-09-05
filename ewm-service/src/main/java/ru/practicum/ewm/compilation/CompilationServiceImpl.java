package ru.practicum.ewm.compilation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.OffsetPageRequest;
import ru.practicum.ewm.event.Event;
import ru.practicum.ewm.event.EventService;
import ru.practicum.ewm.event.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

	private final CompilationRepository compilationRepository;
	private final EventService eventService;

	@Override
	@Transactional
	public CompilationDto create(NewCompilationDto dto) {
		Compilation compilation = new Compilation();
		compilation.setTitle(dto.getTitle());
		compilation.setPinned(Boolean.TRUE.equals(dto.getPinned()));
		compilation.setEvents(loadEvents(dto.getEvents()));
		return toDto(compilationRepository.save(compilation));
	}

	@Override
	@Transactional
	public void delete(Long compId) {
		if (!compilationRepository.existsById(compId)) {
			throw new NotFoundException("Compilation with id=" + compId + " was not found");
		}
		compilationRepository.deleteById(compId);
	}

	@Override
	@Transactional
	public CompilationDto update(Long compId, UpdateCompilationRequest dto) {
		Compilation compilation = compilationRepository.findByIdWithEvents(compId)
				.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
		if (dto.getTitle() != null) {
			compilation.setTitle(dto.getTitle());
		}
		if (dto.getPinned() != null) {
			compilation.setPinned(dto.getPinned());
		}
		if (dto.getEvents() != null) {
			compilation.setEvents(loadEvents(dto.getEvents()));
		}
		return toDto(compilationRepository.save(compilation));
	}

	@Override
	public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
		OffsetPageRequest page = new OffsetPageRequest(from, size);
		List<Compilation> compilations = pinned == null
				? compilationRepository.findAll(page).getContent()
				: compilationRepository.findAllByPinned(pinned, page);
		if (compilations.isEmpty()) {
			return List.of();
		}
		Map<Long, Compilation> fetched = compilationRepository.findAllWithEventsByIdIn(
						compilations.stream().map(Compilation::getId).toList())
				.stream()
				.collect(Collectors.toMap(Compilation::getId, compilation -> compilation));
		return compilations.stream()
				.map(compilation -> toDto(fetched.getOrDefault(compilation.getId(), compilation)))
				.toList();
	}

	@Override
	public CompilationDto getCompilation(Long compId) {
		Compilation compilation = compilationRepository.findByIdWithEvents(compId)
				.orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
		return toDto(compilation);
	}

	private Set<Event> loadEvents(Set<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return new HashSet<>();
		}
		return new HashSet<>(eventService.getEvents(ids));
	}

	private CompilationDto toDto(Compilation compilation) {
		List<Event> events = compilation.getEvents() == null
				? List.of()
				: new ArrayList<>(compilation.getEvents());
		List<EventShortDto> eventDtos = eventService.toShortDtos(events);
		return new CompilationDto(eventDtos, compilation.getId(), compilation.getPinned(), compilation.getTitle());
	}
}
