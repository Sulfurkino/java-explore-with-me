package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {

	public ForbiddenException(String message) {
		super(HttpStatus.CONFLICT, "For the requested operation the conditions are not met.", message);
	}

	@Override
	public HttpStatus getHttpStatus() {
		return HttpStatus.CONFLICT;
	}

	public String getStatusName() {
		return "FORBIDDEN";
	}
}
