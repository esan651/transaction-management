package com.maybank.transaction.specification;

import com.maybank.transaction.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class TransactionSpecification {

	public static Specification<Transaction> hasCustomerId(String customerId) {
		return (root, query, cb) -> {
			if (customerId == null || customerId.isEmpty()) {
				return cb.conjunction();
			}
			Join<Transaction, Account> accountJoin = root.join("account", JoinType.INNER);
			Join<Account, Customer> customerJoin = accountJoin.join("customer", JoinType.INNER);
			return cb.equal(customerJoin.get("id").as(String.class), customerId);
		};
	}

	public static Specification<Transaction> hasAccountNumber(String accountNumber) {
		return (root, query, cb) -> {
			if (accountNumber == null || accountNumber.isEmpty()) {
				return cb.conjunction();
			}
			Join<Transaction, Account> accountJoin = root.join("account", JoinType.INNER);
			return cb.equal(accountJoin.get("accountNumber"), accountNumber);
		};
	}

	public static Specification<Transaction> hasDescriptionLike(String description) {
		return (root, query, cb) -> {
			if (description == null || description.isEmpty()) {
				return cb.conjunction();
			}
			return cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%");
		};
	}

}
