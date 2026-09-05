package ru.practicum.ewm.stats.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EndpointHitJsonTest {

	private ObjectMapper mapper;

	@BeforeEach
	void setUp() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Test
	void deserializeTimestampWithSpaceSeparator() throws Exception {
		String json = "{"
				+ "\"app\":\"ewm-main-service\","
				+ "\"uri\":\"/events/1\","
				+ "\"ip\":\"192.163.0.1\","
				+ "\"timestamp\":\"2022-09-06 11:00:23\""
				+ "}";

		EndpointHit hit = mapper.readValue(json, EndpointHit.class);

		assertThat(hit.getApp()).isEqualTo("ewm-main-service");
		assertThat(hit.getUri()).isEqualTo("/events/1");
		assertThat(hit.getIp()).isEqualTo("192.163.0.1");
		assertThat(hit.getTimestamp()).isEqualTo(LocalDateTime.of(2022, 9, 6, 11, 0, 23));
	}

	@Test
	void serializeTimestampWithSpaceSeparator() throws Exception {
		EndpointHit hit = new EndpointHit();
		hit.setApp("ewm-main-service");
		hit.setUri("/events/1");
		hit.setIp("192.163.0.1");
		hit.setTimestamp(LocalDateTime.of(2022, 9, 6, 11, 0, 23));

		String json = mapper.writeValueAsString(hit);

		assertThat(json).contains("\"timestamp\":\"2022-09-06 11:00:23\"");
		assertThat(json).doesNotContain("2022-09-06T11:00:23");
	}

	@Test
	void serializeViewStatsHitsAsNumber() throws Exception {
		ViewStats stats = new ViewStats("ewm-main-service", "/events/1", 6L);

		String json = mapper.writeValueAsString(stats);

		assertThat(json).contains("\"app\":\"ewm-main-service\"");
		assertThat(json).contains("\"uri\":\"/events/1\"");
		assertThat(json).contains("\"hits\":6");
	}
}
