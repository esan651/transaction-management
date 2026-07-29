package com.maybank.transaction.batch;

import com.maybank.transaction.entity.Transaction;
import com.maybank.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TransactionBatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;
	private final TransactionItemProcessor transactionItemProcessor;

	@Bean
	public FlatFileItemReader<TransactionRaw> reader() {
		return new FlatFileItemReaderBuilder<TransactionRaw>()
				.name("transactionItemReader")
				.resource(new ClassPathResource("dataSource.txt"))
				.linesToSkip(1)
				.delimited()
				.delimiter("|")
				.names("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId")
				.fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
					setTargetType(TransactionRaw.class);
				}})
				.build();
	}

	@Bean
	public ItemWriter<Transaction> writer(TransactionRepository repository) {
		return repository::saveAll;
	}

	@Bean
	public Step importStep(ItemReader<TransactionRaw> reader,
	                       ItemProcessor<TransactionRaw, Transaction> processor,
	                       ItemWriter<Transaction> writer) {
		return new StepBuilder("importStep", jobRepository)
				.<TransactionRaw, Transaction>chunk(10, transactionManager)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()
				.skip(IllegalArgumentException.class)
				.skipLimit(5)
				.build();
	}

	@Bean
	public Job importTransactionJob(Step importStep, JobCompletionNotificationListener listener) {
		return new JobBuilder("importTransactionJob", jobRepository)
				.listener(listener)
				.start(importStep)
				.build();
	}
}