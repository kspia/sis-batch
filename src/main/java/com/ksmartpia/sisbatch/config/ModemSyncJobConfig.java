package com.ksmartpia.sisbatch.config;

import com.ksmartpia.sisbatch.listeners.StepLoggingListener;
import com.ksmartpia.sisbatch.listeners.JobLoggingListener;
import com.ksmartpia.sisbatch.listeners.SyncTimeUpdateListener;
import com.ksmartpia.sisbatch.model.ModemEquip;
import com.ksmartpia.sisbatch.processor.ModemProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ModemSyncJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager batchTxManager;
    private final ModemProcessor modemProcessor;

    private final StepLoggingListener stepLoggingListener;
    private final JobLoggingListener jobLoggingListener;
    private final SyncTimeUpdateListener syncTimeUpdateListener;

    @Bean
    public Job modemSyncJob(Step modemSyncStep) {
        return new JobBuilder("modemSyncJob", jobRepository)
            .listener(jobLoggingListener)
            .listener(syncTimeUpdateListener)
            .start(modemSyncStep)
            .build();
    }

    @Bean
    public Step modemSyncStep(
        JdbcCursorItemReader<ModemEquip> reader,
        JdbcBatchItemWriter<ModemEquip> writer
    ) {
        return new StepBuilder("modemSyncStep", jobRepository)
            .<ModemEquip, ModemEquip>chunk(2000, batchTxManager)  // ← 이게 정답
            .reader(reader)
            .processor(modemProcessor)
            .writer(writer)
            .listener(stepLoggingListener)
            .build();
    }
}
