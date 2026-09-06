package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends ApiException {

	public ConflictException(String message) {
		super(HttpStatus.CONFLICT, "Integrity constraint has been violated.", message);
	}

	public ConflictException(String reason, String message) {
		super(HttpStatus.CONFLICT, reason, message);
	}
}
