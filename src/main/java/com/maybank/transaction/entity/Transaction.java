package com.maybank.transaction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions",
		indexes = { @Index(name = "idx_trx_account_id", columnList = "account_id"),
				@Index(name = "idx_trx_date", columnList = "trx_date"),
				@Index(name = "idx_trx_description", columnList = "description") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "account_id", nullable = false)
	private Account account;

	@Column(name = "trx_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal trxAmount;

	@Column(name = "description", nullable = false, length = 255)
	private String description;

	@Column(name = "trx_date", nullable = false)
	private LocalDate trxDate;

	@Column(name = "trx_time", nullable = false)
	private LocalTime trxTime;

	@Version
	@Column(name = "version")
	private Long version;

	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

}
