package ru.practicum.ewm.stats.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.dto.ViewStats;
import ru.practicum.ewm.stats.exception.BadRequestException;
import ru.practicum.ewm.stats.exception.ErrorHandler;
import ru.practicum.ewm.stats.service.StatsService;

@WebMvcTest(StatsController.class)
@Import(ErrorHandler.class)
class StatsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private StatsService statsService;

	@Test
	void hitReturns201() throws Exception {
		String body = "{"
				+ "\"app\":\"ewm-main-service\","
				+ "\"uri\":\"/events/1\","
				+ "\"ip\":\"192.163.0.1\","
				+ "\"timestamp\":\"2022-09-06 11:00:23\""
				+ "}";

		mockMvc.perform(post("/hit")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isCreated());

		verify(statsService).saveHit(any(EndpointHit.class));
	}

	@Test
	void getStatsReturns200() throws Exception {
		when(statsService.getStats(any(), any(), nullable(List.class), anyBoolean()))
				.thenReturn(List.of(new ViewStats("ewm-main-service", "/events/1", 6L)));

		mockMvc.perform(get("/stats")
						.param("start", "2022-09-06 00:00:00")
						.param("end", "2022-09-07 00:00:00")
						.param("uris", "/events/1")
						.param("unique", "false"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].app").value("ewm-main-service"))
				.andExpect(jsonPath("$[0].uri").value("/events/1"))
				.andExpect(jsonPath("$[0].hits").value(6));
	}

	@Test
	void getStatsWhenServiceRejectsRangeReturns400() throws Exception {
		when(statsService.getStats(any(), any(), nullable(List.class), anyBoolean()))
				.thenThrow(new BadRequestException("Start must not be after end"));

		mockMvc.perform(get("/stats")
						.param("start", "2022-09-07 00:00:00")
						.param("end", "2022-09-06 00:00:00"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void getStatsWhenDateIsInvalidReturns400() throws Exception {
		mockMvc.perform(get("/stats")
						.param("start", "not-a-date")
						.param("end", "2022-09-06 00:00:00"))
				.andExpect(status().isBadRequest());
	}
}
