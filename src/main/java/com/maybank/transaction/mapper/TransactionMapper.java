package com.maybank.transaction.mapper;

import com.maybank.transaction.dto.TransactionDTO;
import com.maybank.transaction.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

	public TransactionDTO toDTO(Transaction transaction) {
		if (transaction == null)
			return null;

		return TransactionDTO.builder()
			.id(transaction.getId())
			.accountNumber(transaction.getAccount() != null ? transaction.getAccount().getAccountNumber() : null)
			.customerId(transaction.getAccount() != null && transaction.getAccount().getCustomer() != null
					? transaction.getAccount().getCustomer().getId() : null)
			.trxAmount(transaction.getTrxAmount())
			.description(transaction.getDescription())
			.trxDate(transaction.getTrxDate())
			.trxTime(transaction.getTrxTime())
			.version(transaction.getVersion())
			.createdAt(transaction.getCreatedAt())
			.updatedAt(transaction.getUpdatedAt())
			.build();
	}

}
