package com.maybank.transaction.controller;

import com.maybank.transaction.dto.*;
import com.maybank.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Endpoints for retrieving and updating transaction records")
@SecurityRequirement(name = "Bearer Authentication")
public class TransactionController {

	private final TransactionService transactionService;

	@Operation(summary = "Search and retrieve paginated transactions",
			description = "Retrieve transactions with optional filtering by customer ID, account number, or description. Supports pagination and sorting via Pageable.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved transactions",
					content = @Content(schema = @Schema(implementation = PagedResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized - JWT token missing or invalid"),
			@ApiResponse(responseCode = "403", description = "Forbidden - insufficient privileges") })
	@GetMapping
	public ResponseEntity<PagedResponse<TransactionDTO>> searchTransactions(
			@ParameterObject @Valid TransactionSearchRequest request, @ParameterObject @PageableDefault(size = 10,
					sort = "trxDate", direction = Sort.Direction.DESC) Pageable pageable) {

		return ResponseEntity.ok(transactionService.searchTransactions(request, pageable));
	}

	@Operation(summary = "Get transaction by ID",
			description = "Retrieve a single transaction record by its unique identifier")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Transaction found",
					content = @Content(schema = @Schema(implementation = TransactionDTO.class))),
			@ApiResponse(responseCode = "404", description = "Transaction not found"),
			@ApiResponse(responseCode = "401", description = "Unauthorized") })
	@GetMapping("/{id}")
	public ResponseEntity<TransactionDTO> getTransaction(
			@Parameter(description = "Transaction ID", example = "1") @PathVariable Long id) {
		return ResponseEntity.ok(transactionService.getTransactionById(id));
	}

	@Operation(summary = "Update transaction description",
			description = "Update the description of a transaction. Requires the current version for optimistic locking to handle concurrent updates.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Update successful",
					content = @Content(schema = @Schema(implementation = TransactionDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request - validation failed"),
			@ApiResponse(responseCode = "404", description = "Transaction not found"),
			@ApiResponse(responseCode = "409",
					description = "Conflict - record was modified by another user (optimistic locking)"),
			@ApiResponse(responseCode = "401", description = "Unauthorized") })
	@PutMapping("/{id}")
	public ResponseEntity<TransactionDTO> updateTransaction(
			@Parameter(description = "Transaction ID", example = "1") @PathVariable Long id,
			@Valid @RequestBody UpdateTransactionRequest request) {
		return ResponseEntity.ok(transactionService.updateTransaction(id, request));
	}

}