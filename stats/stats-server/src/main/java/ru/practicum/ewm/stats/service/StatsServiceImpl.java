package ru.practicum.ewm.stats.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.exception.BadRequestException;
import ru.practicum.ewm.stats.model.Hit;
import ru.practicum.ewm.stats.repository.HitRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {

	private final HitRepository hitRepository;

	@Override
	@Transactional
	public void saveHit(EndpointHit hitDto) {
		Hit hit = new Hit();
		hit.setApp(hitDto.getApp());
		hit.setUri(hitDto.getUri());
		hit.setIp(hitDto.getIp());
		hit.setTimestamp(hitDto.getTimestamp());
		hitRepository.save(hit);
	}

	@Override
	public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
		if (start.isAfter(end)) {
			throw new BadRequestException("Start must not be after end");
		}
		boolean filterUris = uris != null && !uris.isEmpty();
		if (filterUris) {
			return unique
					? hitRepository.findUniqueStatsByUris(start, end, uris)
					: hitRepository.findStatsByUris(start, end, uris);
		}
		return unique
				? hitRepository.findUniqueStats(start, end)
				: hitRepository.findStats(start, end);
	}
}
