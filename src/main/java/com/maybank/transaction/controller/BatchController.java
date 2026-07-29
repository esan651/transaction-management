package com.maybank.transaction.controller;

import com.maybank.transaction.dto.BatchResponse;
import com.maybank.transaction.service.BatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
@Tag(name = "Batch Processing", description = "Endpoints for running Spring Batch jobs")
@SecurityRequirement(name = "Bearer Authentication")
public class BatchController {

	private final BatchService batchService;

	@Operation(summary = "Run transaction import batch job",
			description = "Triggers the Spring Batch job to import transaction data from datasource file into database. Requires ADMIN role.")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Batch job started successfully",
					content = @Content(schema = @Schema(implementation = BatchResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "403", description = "Forbidden - requires ADMIN role"),
			@ApiResponse(responseCode = "409", description = "Batch job already running") })
	@PostMapping("/run")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<BatchResponse> runBatchJob() {
		return ResponseEntity.ok(batchService.runImportTransactionJob());
	}

}
