package com.maybank.transaction.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex,
			HttpServletRequest request) {
		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.NOT_FOUND.value())
			.error("Not Found")
			.message(ex.getMessage())
			.path(request.getRequestURI())
			.build();
		return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(OptimisticLockException.class)
	public ResponseEntity<ErrorResponse> handleOptimisticLock(OptimisticLockException ex, HttpServletRequest request) {
		log.warn("Concurrent update detected: {}", ex.getMessage());
		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.CONFLICT.value())
			.error("Conflict")
			.message("The record was modified by another user. Please refresh and try again.")
			.path(request.getRequestURI())
			.build();
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(ConcurrentUpdateException.class)
	public ResponseEntity<ErrorResponse> handleConcurrentUpdate(ConcurrentUpdateException ex,
			HttpServletRequest request) {
		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.CONFLICT.value())
			.error("Conflict")
			.message(ex.getMessage())
			.path(request.getRequestURI())
			.build();
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(BatchExecutionException.class)
	public ResponseEntity<ErrorResponse> handleBatchExecution(BatchExecutionException ex, HttpServletRequest request) {
		log.error("Batch execution failed", ex);
		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.CONFLICT.value())
			.error("Batch Execution Failed")
			.message(ex.getMessage())
			.path(request.getRequestURI())
			.build();
		return new ResponseEntity<>(error, HttpStatus.CONFLICT);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.UNAUTHORIZED.value())
			.error("Unauthorized")
			.message("Invalid username or password")
			.path(request.getRequestURI())
			.build();
		return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.FORBIDDEN.value())
			.error("Forbidden")
			.message("You do not have permission to access this resource")
			.path(request.getRequestURI())
			.build();
		return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach(error -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});

		Map<String, Object> response = new HashMap<>();
		response.put("timestamp", LocalDateTime.now());
		response.put("status", HttpStatus.BAD_REQUEST.value());
		response.put("errors", errors);

		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
		log.error("Unexpected error: ", ex);
		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
			.error("Internal Server Error")
			.message("An unexpected error occurred. Please contact support.")
			.path(request.getRequestURI())
			.build();
		return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
