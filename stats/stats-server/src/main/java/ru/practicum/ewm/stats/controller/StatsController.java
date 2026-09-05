package ru.practicum.ewm.stats.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.service.StatsService;

@RestController
@RequiredArgsConstructor
public class StatsController {

	private final StatsService statsService;

	@PostMapping("/hit")
	@ResponseStatus(HttpStatus.CREATED)
	public void hit(@RequestBody EndpointHit hit) {
		statsService.saveHit(hit);
	}

	@GetMapping("/stats")
	public List<ViewStats> getStats(
			@RequestParam String start,
			@RequestParam String end,
			@RequestParam(required = false) List<String> uris,
			@RequestParam(defaultValue = "false") boolean unique) {
		return statsService.getStats(StatsDateTime.parse(start), StatsDateTime.parse(end), uris, unique);
	}
}
