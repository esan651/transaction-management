package com.maybank.transaction.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSearchRequest {

	private String customerId;

	private String accountNumber;

	private String description;

}
