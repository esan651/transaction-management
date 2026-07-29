package com.maybank.transaction.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("Starting batch job: {}", jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("Batch job completed successfully. Read count: {}, Write count: {}",
					jobExecution.getStepExecutions().iterator().next().getReadCount(),
					jobExecution.getStepExecutions().iterator().next().getWriteCount());
		}
		else {
			log.error("Batch job failed with status: {}", jobExecution.getStatus());
		}
	}

}
