package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ApiError handleNotFound(NotFoundException exception) {
		return apiError(exception.getHttpStatus().name(), exception.getReason(), exception.getMessage(), exception);
	}

	@ExceptionHandler(ForbiddenException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleForbidden(ForbiddenException exception) {
		return apiError(exception.getStatusName(), exception.getReason(), exception.getMessage(), exception);
	}

	@ExceptionHandler(ConflictException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleConflict(ConflictException exception) {
		return apiError(exception.getHttpStatus().name(), exception.getReason(), exception.getMessage(), exception);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	public ApiError handleIntegrity(DataIntegrityViolationException exception) {
		return apiError(HttpStatus.CONFLICT.name(), "Integrity constraint has been violated.",
				exception.getMessage(), exception);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleValidation(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.map(error -> "Field: " + error.getField() + ". Error: " + error.getDefaultMessage()
						+ ". Value: " + error.getRejectedValue())
				.collect(Collectors.joining("; "));
		return apiError(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", message, exception);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleConstraint(ConstraintViolationException exception) {
		return apiError(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", exception.getMessage(), exception);
	}

	@ExceptionHandler({
			MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class,
			HttpMessageNotReadableException.class,
			IllegalArgumentException.class
	})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ApiError handleBadRequest(Exception exception) {
		return apiError(HttpStatus.BAD_REQUEST.name(), "Incorrectly made request.", exception.getMessage(), exception);
	}

	private ApiError apiError(String status, String reason, String message, Exception exception) {
		log.warn("API error: status={}, reason={}, message={}", status, reason, message, exception);
		return new ApiError(List.of(), message, reason, status, LocalDateTime.now());
	}
}
