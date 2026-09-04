package ru.practicum.ewm.stats.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.model.Hit;

@DataJpaTest
@ActiveProfiles("test")
class HitRepositoryTest {

	@Autowired
	private HitRepository hitRepository;

	@BeforeEach
	void setUp() {
		hitRepository.save(hit("ewm-main-service", "/events", "1.1.1.1", LocalDateTime.of(2022, 9, 6, 11, 0, 0)));
		hitRepository.save(hit("ewm-main-service", "/events", "1.1.1.1", LocalDateTime.of(2022, 9, 6, 12, 0, 0)));
		hitRepository.save(hit("ewm-main-service", "/events", "2.2.2.2", LocalDateTime.of(2022, 9, 6, 13, 0, 0)));
		hitRepository.save(hit("ewm-main-service", "/events/1", "3.3.3.3", LocalDateTime.of(2022, 9, 6, 14, 0, 0)));
		hitRepository.save(hit("ewm-main-service", "/events", "9.9.9.9", LocalDateTime.of(2022, 9, 1, 0, 0, 0)));
	}

	@Test
	void findStatsCountsAllHitsAndOrdersByHitsDesc() {
		List<ViewStats> result = hitRepository.findStats(
				LocalDateTime.of(2022, 9, 6, 0, 0, 0),
				LocalDateTime.of(2022, 9, 6, 23, 59, 59));

		assertThat(result).extracting(ViewStats::getUri).containsExactly("/events", "/events/1");
		assertThat(result.get(0).getHits()).isEqualTo(3L);
		assertThat(result.get(1).getHits()).isEqualTo(1L);
	}

	@Test
	void findUniqueStatsCountsDistinctIp() {
		List<ViewStats> result = hitRepository.findUniqueStats(
				LocalDateTime.of(2022, 9, 6, 0, 0, 0),
				LocalDateTime.of(2022, 9, 6, 23, 59, 59));

		assertThat(result).filteredOn(stats -> "/events".equals(stats.getUri()))
				.extracting(ViewStats::getHits)
				.containsExactly(2L);
	}

	@Test
	void findStatsByUrisFiltersList() {
		List<ViewStats> result = hitRepository.findStatsByUris(
				LocalDateTime.of(2022, 9, 6, 0, 0, 0),
				LocalDateTime.of(2022, 9, 6, 23, 59, 59),
				List.of("/events/1"));

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getUri()).isEqualTo("/events/1");
		assertThat(result.get(0).getHits()).isEqualTo(1L);
	}

	private Hit hit(String app, String uri, String ip, LocalDateTime timestamp) {
		Hit hit = new Hit();
		hit.setApp(app);
		hit.setUri(uri);
		hit.setIp(ip);
		hit.setTimestamp(timestamp);
		return hit;
	}
}
