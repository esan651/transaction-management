package com.maybank.transaction.service;

import com.maybank.transaction.dto.*;
import org.springframework.data.domain.Pageable;

public interface TransactionService {

	PagedResponse<TransactionDTO> searchTransactions(TransactionSearchRequest request, Pageable pageable);

	TransactionDTO getTransactionById(Long id);

	TransactionDTO updateTransaction(Long id, UpdateTransactionRequest request);

}
