package com.maybank.transaction.controller;

import com.maybank.transaction.dto.AuthRequest;
import com.maybank.transaction.dto.AuthResponse;
import com.maybank.transaction.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user authentication and JWT token management")
public class AuthController {

	private final AuthService authService;

	@Operation(summary = "Authenticate user and get JWT token",
			description = "Login with username and password to receive a Bearer JWT token for subsequent API calls")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Authentication successful",
					content = @Content(schema = @Schema(implementation = AuthResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request body - validation failed"),
			@ApiResponse(responseCode = "401", description = "Invalid username or password") })
	@PostMapping("/login")
	public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {

		return ResponseEntity.ok(authService.authenticate(request));
	}

}
