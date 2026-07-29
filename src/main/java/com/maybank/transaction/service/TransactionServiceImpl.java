package com.maybank.transaction.service;

import com.maybank.transaction.dto.*;
import com.maybank.transaction.entity.Transaction;
import com.maybank.transaction.exception.ConcurrentUpdateException;
import com.maybank.transaction.exception.ResourceNotFoundException;
import com.maybank.transaction.mapper.TransactionMapper;
import com.maybank.transaction.repository.AccountRepository;
import com.maybank.transaction.repository.TransactionRepository;
import com.maybank.transaction.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionServiceImpl implements TransactionService {

	private final TransactionRepository transactionRepository;

	private final AccountRepository accountRepository;

	private final TransactionMapper transactionMapper;

	@Override
	public PagedResponse<TransactionDTO> searchTransactions(TransactionSearchRequest request, Pageable pageable) {
		log.info("Searching transactions with criteria: {}, pageable: {}", request, pageable);

		Specification<Transaction> spec = Specification.where(null);

		if (request.getCustomerId() != null && !request.getCustomerId().isEmpty()) {
			spec = spec.and(TransactionSpecification.hasCustomerId(request.getCustomerId()));
		}

		if (request.getAccountNumber() != null && !request.getAccountNumber().isEmpty()) {
			spec = spec.and(TransactionSpecification.hasAccountNumber(request.getAccountNumber()));
		}

		if (request.getDescription() != null && !request.getDescription().isEmpty()) {
			spec = spec.and(TransactionSpecification.hasDescriptionLike(request.getDescription()));
		}

		Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

		List<TransactionDTO> content = transactionPage.getContent()
			.stream()
			.map(transactionMapper::toDTO)
			.collect(Collectors.toList());

		return PagedResponse.<TransactionDTO>builder()
			.content(content)
			.pageNumber(transactionPage.getNumber())
			.pageSize(transactionPage.getSize())
			.totalElements(transactionPage.getTotalElements())
			.totalPages(transactionPage.getTotalPages())
			.last(transactionPage.isLast())
			.first(transactionPage.isFirst())
			.build();
	}

	@Override
	public TransactionDTO getTransactionById(Long id) {
		log.info("Fetching transaction by id: {}", id);
		Transaction transaction = transactionRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
		return transactionMapper.toDTO(transaction);
	}

	@Override
	@Transactional
	public TransactionDTO updateTransaction(Long id, UpdateTransactionRequest request) {
		log.info("Updating transaction id: {} with version: {}", id, request.getVersion());

		Transaction transaction = transactionRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));

		if (!transaction.getVersion().equals(request.getVersion())) {
			throw new ConcurrentUpdateException("Transaction was modified by another user. Current version: "
					+ transaction.getVersion() + ", Your version: " + request.getVersion());
		}

		transaction.setDescription(request.getDescription());

		Transaction updated = transactionRepository.save(transaction);
		log.info("Transaction {} updated successfully. New version: {}", id, updated.getVersion());

		return transactionMapper.toDTO(updated);
	}

}
