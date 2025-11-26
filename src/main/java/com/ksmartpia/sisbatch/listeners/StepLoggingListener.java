package com.ksmartpia.sisbatch.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StepLoggingListener implements StepExecutionListener {

    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("[STEP START] {}", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("[STEP END] {}", stepExecution.getStepName());
        log.info("  READ  : {}", stepExecution.getReadCount());
        log.info("  WRITE : {}", stepExecution.getWriteCount());
        log.info("  SKIP  : {}", stepExecution.getSkipCount());
        log.info("  COMMITS : {}", stepExecution.getCommitCount());
        return stepExecution.getExitStatus();
    }
}
