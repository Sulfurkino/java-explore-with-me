package ru.practicum.ewm.stats.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;

class StatsClientTest {

	private StatsClient statsClient;
	private MockRestServiceServer server;

	@BeforeEach
	void setUp() {
		RestTemplate restTemplate = new RestTemplate();
		server = MockRestServiceServer.createServer(restTemplate);
		statsClient = new StatsClient(restTemplate, "http://localhost:9090");
	}

	@Test
	void hitPostsJsonToHitEndpoint() {
		EndpointHit hit = new EndpointHit(null, "ewm-main-service", "/events/1", "192.163.0.1",
				LocalDateTime.of(2022, 9, 6, 11, 0, 23));
		server.expect(requestTo("http://localhost:9090/hit"))
				.andExpect(method(HttpMethod.POST))
				.andRespond(withStatus(HttpStatus.CREATED));

		statsClient.hit(hit);

		server.verify();
	}

	@Test
	void getStatsEncodesDatesAndReadsViewStats() {
		server.expect(requestTo(org.hamcrest.Matchers.allOf(
						org.hamcrest.Matchers.containsString("http://localhost:9090/stats?"),
						org.hamcrest.Matchers.containsString("start=2022-09-06%2000:00:00"),
						org.hamcrest.Matchers.containsString("end=2022-09-07%2000:00:00"),
						org.hamcrest.Matchers.containsString("unique=true"),
						org.hamcrest.Matchers.containsString("uris=/events/1"))))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withStatus(HttpStatus.OK)
						.contentType(MediaType.APPLICATION_JSON)
						.body("[{\"app\":\"ewm-main-service\",\"uri\":\"/events/1\",\"hits\":6}]"));

		List<ViewStats> result = statsClient.getStats(
				LocalDateTime.of(2022, 9, 6, 0, 0, 0),
				LocalDateTime.of(2022, 9, 7, 0, 0, 0),
				List.of("/events/1"),
				true);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).getHits()).isEqualTo(6L);
		server.verify();
	}
}
