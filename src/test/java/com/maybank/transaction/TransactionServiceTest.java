package com.maybank.transaction;

import com.maybank.transaction.dto.*;
import com.maybank.transaction.entity.*;
import com.maybank.transaction.exception.ConcurrentUpdateException;
import com.maybank.transaction.exception.ResourceNotFoundException;
import com.maybank.transaction.mapper.TransactionMapper;
import com.maybank.transaction.repository.AccountRepository;
import com.maybank.transaction.repository.TransactionRepository;
import com.maybank.transaction.service.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private AccountRepository accountRepository;

	@Mock
	private TransactionMapper transactionMapper;

	@InjectMocks
	private TransactionServiceImpl transactionService;

	private Transaction transaction;

	private TransactionDTO transactionDTO;

	@BeforeEach
	void setUp() {
		Customer customer = Customer.builder().id(222L).name("Customer A").build();
		Account account = Account.builder().id(1L).accountNumber("8872838283").customer(customer).build();

		transaction = Transaction.builder()
			.id(1L)
			.account(account)
			.trxAmount(new BigDecimal("123.00"))
			.description("FUND TRANSFER")
			.trxDate(LocalDate.of(2019, 9, 12))
			.trxTime(LocalTime.of(11, 11, 11))
			.version(0L)
			.build();

		transactionDTO = TransactionDTO.builder()
			.id(1L)
			.accountNumber("8872838283")
			.customerId(222L)
			.trxAmount(new BigDecimal("123.00"))
			.description("FUND TRANSFER")
			.trxDate(LocalDate.of(2019, 9, 12))
			.trxTime(LocalTime.of(11, 11, 11))
			.version(0L)
			.build();
	}

	@Test
	void searchTransactions_ShouldReturnPagedResponse() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by("trxDate").descending());
		Page<Transaction> page = new PageImpl<>(Collections.singletonList(transaction), pageable, 1);

		when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);
		when(transactionMapper.toDTO(any())).thenReturn(transactionDTO);

		TransactionSearchRequest request = TransactionSearchRequest.builder().customerId("222").build();

		PagedResponse<TransactionDTO> result = transactionService.searchTransactions(request, pageable);

		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals(0, result.getPageNumber());
		assertEquals(10, result.getPageSize());
		assertEquals(1, result.getTotalElements());
		verify(transactionRepository).findAll(any(Specification.class), any(Pageable.class));
	}

	@Test
	void searchTransactions_WithAllFilters_ShouldBuildSpecificationCorrectly() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<Transaction> page = new PageImpl<>(Collections.emptyList(), pageable, 0);

		when(transactionRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

		TransactionSearchRequest request = TransactionSearchRequest.builder()
			.customerId("222")
			.accountNumber("8872838283")
			.description("FUND")
			.build();

		transactionService.searchTransactions(request, pageable);

		verify(transactionRepository).findAll(any(Specification.class), eq(pageable));
	}

	@Test
	void getTransactionById_ShouldReturnTransaction() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
		when(transactionMapper.toDTO(transaction)).thenReturn(transactionDTO);

		TransactionDTO result = transactionService.getTransactionById(1L);

		assertNotNull(result);
		assertEquals("8872838283", result.getAccountNumber());
		assertEquals(222L, result.getCustomerId());
	}

	@Test
	void getTransactionById_ShouldThrowWhenNotFound() {
		when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
				() -> transactionService.getTransactionById(999L));

		assertTrue(exception.getMessage().contains("Transaction not found with id : '999'"));
	}

	@Test
	void updateTransaction_ShouldUpdateDescriptionAndIncrementVersion() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));
		when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
			Transaction saved = invocation.getArgument(0);
			saved.setVersion(1L);
			return saved;
		});
		when(transactionMapper.toDTO(any())).thenReturn(transactionDTO);

		UpdateTransactionRequest request = UpdateTransactionRequest.builder()
			.description("UPDATED DESCRIPTION")
			.version(0L)
			.build();

		TransactionDTO result = transactionService.updateTransaction(1L, request);

		assertNotNull(result);
		verify(transactionRepository).save(any(Transaction.class));
	}

	@Test
	void updateTransaction_ShouldThrowConcurrentUpdateException_WhenVersionMismatch() {
		when(transactionRepository.findById(1L)).thenReturn(Optional.of(transaction));

		UpdateTransactionRequest request = UpdateTransactionRequest.builder()
			.description("UPDATED DESCRIPTION")
			.version(5L)
			.build();

		ConcurrentUpdateException exception = assertThrows(ConcurrentUpdateException.class,
				() -> transactionService.updateTransaction(1L, request));

		assertTrue(exception.getMessage().contains("modified by another user"));
		verify(transactionRepository, never()).save(any());
	}

	@Test
	void updateTransaction_ShouldThrowWhenTransactionNotFound() {
		when(transactionRepository.findById(999L)).thenReturn(Optional.empty());

		UpdateTransactionRequest request = UpdateTransactionRequest.builder()
			.description("UPDATED")
			.version(0L)
			.build();

		assertThrows(ResourceNotFoundException.class, () -> transactionService.updateTransaction(999L, request));
	}

}