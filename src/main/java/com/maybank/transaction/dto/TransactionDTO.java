package com.maybank.transaction.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

	private Long id;

	private String accountNumber;

	private Long customerId;

	private BigDecimal trxAmount;

	private String description;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate trxDate;

	@JsonFormat(pattern = "HH:mm:ss")
	private LocalTime trxTime;

	private Long version;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;

}
