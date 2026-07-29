package com.maybank.transaction.batch;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class TransactionFieldSetMapper implements FieldSetMapper<TransactionRaw> {

	@Override
	public TransactionRaw mapFieldSet(FieldSet fieldSet) throws BindException {
		TransactionRaw raw = new TransactionRaw();
		raw.setAccountNumber(fieldSet.readString("accountNumber"));
		raw.setTrxAmount(fieldSet.readString("trxAmount"));
		raw.setDescription(fieldSet.readString("description"));
		raw.setTrxDate(fieldSet.readString("trxDate"));
		raw.setTrxTime(fieldSet.readString("trxTime"));
		raw.setCustomerId(fieldSet.readString("customerId"));
		return raw;
	}

}
