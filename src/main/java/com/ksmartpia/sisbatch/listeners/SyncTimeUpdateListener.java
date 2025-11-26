package com.ksmartpia.sisbatch.listeners;

import com.ksmartpia.sisbatch.dao.SyncMetaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncTimeUpdateListener extends JobExecutionListenerSupport {

    private final SyncMetaRepository syncMetaRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("[SyncTimeUpdate] afterJob called. status={}", jobExecution.getStatus());

        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            LocalDateTime now = LocalDateTime.now();
            log.info("[SyncTimeUpdate] Update lastSyncTime={}", now);
            syncMetaRepository.updateLastSyncTime("MODEM_SYNC", now);
        }
    }
}
