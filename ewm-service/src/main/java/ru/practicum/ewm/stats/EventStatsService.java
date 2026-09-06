package ru.practicum.ewm.stats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

@Service
@RequiredArgsConstructor
public class EventStatsService {

	public static final String APP = "ewm-main-service";

	private final StatsClient statsClient;

	public void hit(String uri, String ip) {
		EndpointHit hit = new EndpointHit();
		hit.setApp(APP);
		hit.setUri(uri);
		hit.setIp(ip);
		hit.setTimestamp(LocalDateTime.now());
		statsClient.hit(hit);
	}

	public Map<Long, Long> getViews(Collection<Long> eventIds) {
		if (eventIds == null || eventIds.isEmpty()) {
			return Collections.emptyMap();
		}
		List<String> uris = eventIds.stream()
				.map(id -> "/events/" + id)
				.toList();
		List<ViewStats> stats = statsClient.getStats(
				LocalDateTime.of(1970, 1, 1, 0, 0, 0),
				LocalDateTime.now().plusYears(100),
				uris,
				true);
		Map<Long, Long> views = new HashMap<>();
		for (ViewStats stat : stats) {
			String uri = stat.getUri();
			int slash = uri.lastIndexOf('/');
			if (slash >= 0 && slash < uri.length() - 1) {
				views.put(Long.parseLong(uri.substring(slash + 1)), stat.getHits());
			}
		}
		return views;
	}
}
