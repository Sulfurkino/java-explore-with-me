package ru.practicum.ewm.stats.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import ru.practicum.ewm.stats.exception.BadRequestException;

final class StatsDateTime {

	static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

	private StatsDateTime() {
	}

	static LocalDateTime parse(String value) {
		if (value == null || value.isBlank()) {
			throw new BadRequestException("Date must not be blank");
		}
		String normalized = URLDecoder.decode(value, StandardCharsets.UTF_8).replace('T', ' ');
		try {
			return LocalDateTime.parse(normalized, FORMATTER);
		} catch (DateTimeParseException exception) {
			throw new BadRequestException("Cannot parse date: " + value);
		}
	}
}
