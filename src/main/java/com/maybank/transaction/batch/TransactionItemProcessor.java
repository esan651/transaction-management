package com.maybank.transaction.batch;

import com.maybank.transaction.entity.Account;
import com.maybank.transaction.entity.Transaction;
import com.maybank.transaction.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionItemProcessor implements ItemProcessor<TransactionRaw, Transaction> {

	private final AccountRepository accountRepository;

	@Override
	public Transaction process(TransactionRaw raw) {
		if ("ACCOUNT_NUMBER".equals(raw.getAccountNumber())) {
			log.warn("Skipping header row");
			return null;
		}

		Account account = accountRepository.findByAccountNumber(raw.getAccountNumber())
				.orElseThrow(() -> new IllegalArgumentException(
						"Account not found: " + raw.getAccountNumber()));

		return Transaction.builder()
				.account(account)
				.trxAmount(new BigDecimal(raw.getTrxAmount()))
				.description(raw.getDescription())
				.trxDate(LocalDate.parse(raw.getTrxDate()))
				.trxTime(LocalTime.parse(raw.getTrxTime()))
				.build();
	}
}