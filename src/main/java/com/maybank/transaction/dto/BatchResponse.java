package com.maybank.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Batch job execution response")
public class BatchResponse {

	@Schema(description = "Unique job execution ID", example = "1")
	private Long jobId;

	@Schema(description = "Job execution status", example = "COMPLETED")
	private String status;

	@Schema(description = "Job start time")
	private LocalDateTime startTime;

	@Schema(description = "Human-readable message", example = "Transaction import batch executed successfully")
	private String message;

}
