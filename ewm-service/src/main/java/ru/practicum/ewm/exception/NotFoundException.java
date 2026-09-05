package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends ApiException {

	public NotFoundException(String message) {
		super(HttpStatus.NOT_FOUND, "The required object was not found.", message);
	}
}
