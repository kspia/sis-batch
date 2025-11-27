package com.ksmartpia.sisbatch.scheduler;

import com.ksmartpia.sisbatch.service.PhotoCompressorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PhotoCompressScheduler {

    private final PhotoCompressorService photoCompressorService;

    @Scheduled(cron = "0 0 0 29 11 *")
    public void runPhotoCompressBatch() {
        log.info("==== PHOTO COMPRESS BATCH START ====");
        try {
            photoCompressorService.run();
        } catch (Exception e) {
            log.error("PHOTO COMPRESS BATCH ERROR: {}", e.getMessage(), e);
        }
        log.info("==== PHOTO COMPRESS BATCH END ====");
    }
}
