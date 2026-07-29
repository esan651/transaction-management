package com.maybank.transaction.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

	private String token;

	private String type = "Bearer";

	private String username;

	private String role;

}
