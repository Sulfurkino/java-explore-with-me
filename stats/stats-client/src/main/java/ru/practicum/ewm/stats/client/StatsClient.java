package ru.practicum.ewm.stats.client;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

public class StatsClient {

	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private final RestTemplate restTemplate;
	private final String serverUrl;

	public StatsClient(RestTemplate restTemplate, String serverUrl) {
		this.restTemplate = restTemplate;
		this.serverUrl = serverUrl;
	}

	public void hit(EndpointHit hit) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<EndpointHit> request = new HttpEntity<>(hit, headers);
		restTemplate.exchange(serverUrl + "/hit", HttpMethod.POST, request, Void.class);
	}

	public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(serverUrl + "/stats")
				.queryParam("start", start.format(DATE_TIME))
				.queryParam("end", end.format(DATE_TIME))
				.queryParam("unique", unique != null && unique);
		if (uris != null) {
			for (String uri : uris) {
				builder.queryParam("uris", uri);
			}
		}
		ResponseEntity<List<ViewStats>> response = restTemplate.exchange(
				builder.encode().build().toUri(),
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<List<ViewStats>>() { });
		return response.getBody() == null ? Collections.emptyList() : response.getBody();
	}
}
