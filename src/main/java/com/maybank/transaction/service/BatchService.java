package com.maybank.transaction.service;

import com.maybank.transaction.dto.BatchResponse;
import com.maybank.transaction.exception.BatchExecutionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatchService {

	private final JobLauncher jobLauncher;

	private final Job importTransactionJob;

	public BatchResponse runImportTransactionJob() {
		try {
			JobParameters parameters = new JobParametersBuilder().addLong("timestamp", System.currentTimeMillis())
				.toJobParameters();

			JobExecution execution = jobLauncher.run(importTransactionJob, parameters);

			log.info("Transaction import batch executed. JobId={}, Status={}", execution.getId(),
					execution.getStatus());

			return BatchResponse.builder()
				.jobId(execution.getId())
				.status(execution.getStatus().name())
				.startTime(execution.getStartTime())
				.message("Transaction import batch executed successfully")
				.build();

		}
		catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			log.error("Failed to execute transaction import batch", e);
			throw new BatchExecutionException("Unable to execute transaction import batch", e);
		}
	}

}
