package ru.practicum.ewm.stats.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.model.Hit;

public interface HitRepository extends JpaRepository<Hit, Long> {

	@Query("""
			SELECT new ru.practicum.ewm.stats.dto.ViewStats(h.app, h.uri, COUNT(h.ip))
			FROM Hit h
			WHERE h.timestamp BETWEEN :start AND :end
			GROUP BY h.app, h.uri
			ORDER BY COUNT(h.ip) DESC
			""")
	List<ViewStats> findStats(LocalDateTime start, LocalDateTime end);

	@Query("""
			SELECT new ru.practicum.ewm.stats.dto.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
			FROM Hit h
			WHERE h.timestamp BETWEEN :start AND :end
			GROUP BY h.app, h.uri
			ORDER BY COUNT(DISTINCT h.ip) DESC
			""")
	List<ViewStats> findUniqueStats(LocalDateTime start, LocalDateTime end);

	@Query("""
			SELECT new ru.practicum.ewm.stats.dto.ViewStats(h.app, h.uri, COUNT(h.ip))
			FROM Hit h
			WHERE h.timestamp BETWEEN :start AND :end
			  AND h.uri IN :uris
			GROUP BY h.app, h.uri
			ORDER BY COUNT(h.ip) DESC
			""")
	List<ViewStats> findStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);

	@Query("""
			SELECT new ru.practicum.ewm.stats.dto.ViewStats(h.app, h.uri, COUNT(DISTINCT h.ip))
			FROM Hit h
			WHERE h.timestamp BETWEEN :start AND :end
			  AND h.uri IN :uris
			GROUP BY h.app, h.uri
			ORDER BY COUNT(DISTINCT h.ip) DESC
			""")
	List<ViewStats> findUniqueStatsByUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
