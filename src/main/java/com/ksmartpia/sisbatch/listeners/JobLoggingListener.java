package com.ksmartpia.sisbatch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JobLoggingListener extends JobExecutionListenerSupport {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("===== MODEM SYNC JOB START =====");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("===== MODEM SYNC JOB END ===== Status = {}", jobExecution.getStatus());
    }
}
