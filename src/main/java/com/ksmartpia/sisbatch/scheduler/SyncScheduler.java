package com.ksmartpia.sisbatch.scheduler;

import com.ksmartpia.sisbatch.dao.SyncMetaRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SyncScheduler {

    private final JobLauncher jobLauncher;
    private final Job modemSyncJob;
    private final SyncMetaRepository syncMetaRepository;

    @Scheduled(cron = "0 58 15 * * *")
    public void runModemSyncJob() throws Exception {

        LocalDateTime lastSyncTime = syncMetaRepository.getLastSyncTime("MODEM_SYNC");
        log.info("▶ [Scheduler] MODEM_SYNC lastSyncTime = {}", lastSyncTime);

        JobParametersBuilder builder = new JobParametersBuilder();

        if (lastSyncTime != null) {
            builder.addString("lastSyncTime", lastSyncTime.toString());
        }

        jobLauncher.run(modemSyncJob, builder.toJobParameters());
    }
}
