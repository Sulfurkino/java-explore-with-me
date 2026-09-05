package ru.practicum.ewm.stats.service;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

public interface StatsService {

	void saveHit(EndpointHit hit);

	List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
