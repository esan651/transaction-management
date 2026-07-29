package com.maybank.transaction.batch;

import lombok.Data;

@Data
public class TransactionRaw {

	private String accountNumber;

	private String trxAmount;

	private String description;

	private String trxDate;

	private String trxTime;

	private String customerId;

}
