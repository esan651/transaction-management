package com.maybank.transaction.service;

import com.maybank.transaction.dto.AuthRequest;
import com.maybank.transaction.dto.AuthResponse;
import com.maybank.transaction.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final AuthenticationManager authenticationManager;

	private final JwtTokenProvider tokenProvider;

	public AuthResponse authenticate(AuthRequest request) {

		Authentication authentication = authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

		String jwt = tokenProvider.generateToken(authentication);

		String role = authentication.getAuthorities()
			.stream()
			.findFirst()
			.map(authority -> authority.getAuthority().replace("ROLE_", ""))
			.orElse(null);

		return AuthResponse.builder().token(jwt).type("Bearer").username(authentication.getName()).role(role).build();
	}

}
