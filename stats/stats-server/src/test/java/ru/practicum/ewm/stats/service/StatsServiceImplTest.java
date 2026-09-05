package ru.practicum.ewm.stats.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.ewm.stats.dto.EndpointHit;
import ru.practicum.ewm.stats.model.Hit;
import ru.practicum.ewm.stats.repository.HitRepository;

@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

	@Mock
	private HitRepository hitRepository;

	@InjectMocks
	private StatsServiceImpl statsService;

	@Test
	void saveHitPersistsAllFields() {
		EndpointHit dto = new EndpointHit(null, "ewm-main-service", "/events/1", "192.163.0.1",
				LocalDateTime.of(2022, 9, 6, 11, 0, 23));
		when(hitRepository.save(any(Hit.class))).thenAnswer(invocation -> invocation.getArgument(0));

		statsService.saveHit(dto);

		ArgumentCaptor<Hit> captor = ArgumentCaptor.forClass(Hit.class);
		verify(hitRepository).save(captor.capture());
		Hit saved = captor.getValue();
		org.assertj.core.api.Assertions.assertThat(saved.getApp()).isEqualTo("ewm-main-service");
		org.assertj.core.api.Assertions.assertThat(saved.getUri()).isEqualTo("/events/1");
		org.assertj.core.api.Assertions.assertThat(saved.getIp()).isEqualTo("192.163.0.1");
		org.assertj.core.api.Assertions.assertThat(saved.getTimestamp())
				.isEqualTo(LocalDateTime.of(2022, 9, 6, 11, 0, 23));
	}

	@Test
	void getStatsWithoutUrisDelegatesToFindStats() {
		LocalDateTime start = LocalDateTime.of(2022, 9, 6, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2022, 9, 6, 23, 0, 0);
		when(hitRepository.findStats(start, end)).thenReturn(List.of());

		statsService.getStats(start, end, null, false);

		verify(hitRepository).findStats(start, end);
	}

	@Test
	void getUniqueStatsWithUrisDelegatesToFindUniqueStatsByUris() {
		LocalDateTime start = LocalDateTime.of(2022, 9, 6, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2022, 9, 6, 23, 0, 0);
		List<String> uris = List.of("/events/1");
		when(hitRepository.findUniqueStatsByUris(start, end, uris)).thenReturn(List.of());

		statsService.getStats(start, end, uris, true);

		verify(hitRepository).findUniqueStatsByUris(start, end, uris);
	}

	@Test
	void getStatsWhenStartAfterEndThrowsBadRequest() {
		LocalDateTime start = LocalDateTime.of(2022, 9, 7, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2022, 9, 6, 0, 0, 0);

		org.assertj.core.api.Assertions.assertThatThrownBy(() -> statsService.getStats(start, end, null, false))
				.isInstanceOf(ru.practicum.ewm.stats.exception.BadRequestException.class);
	}
}
