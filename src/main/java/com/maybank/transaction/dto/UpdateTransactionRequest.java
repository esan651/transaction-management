package com.maybank.transaction.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTransactionRequest {

	@NotBlank(message = "Description is required")
	private String description;

	@NotNull(message = "Version is required for optimistic locking")
	private Long version;

}
