package com.ksmartpia.sisbatch.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoCompressorService {

    private final JdbcTemplate jdbcTemplate;

    // 서버 내 실제 이미지 루트 경로 (운영 환경에 맞게 수정)
    private final Path rootPath = Paths.get("/data/upload");
    private final AtomicInteger totalCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failCount = new AtomicInteger(0);

    public void run() throws InterruptedException {
        // DB에서 모든 이미지 경로 조회
        List<String> photoPaths = jdbcTemplate.queryForList(
            "select photo_path from sis_photo where last_modified_by not in ('kspia') and last_modified_date < '2025-10-30 10:00'",
            String.class
        );

        totalCount.set(photoPaths.size());
        log.info("[PHOTO COMPRESS] 총 {}건 압축 시작", totalCount.get());

        // 병렬 처리 (CPU I/O 밸런스 맞춰 4스레드 추천)
        ExecutorService pool = Executors.newFixedThreadPool(4);

        for (String path : photoPaths) {
            pool.submit(() -> compressIfNeeded(path));
        }

        pool.shutdown();
        pool.awaitTermination(24, TimeUnit.HOURS);

        log.info("[PHOTO COMPRESS] 모든 압축 완료");
        log.info("[PHOTO COMPRESS] 총: {}, 성공: {}, 실패: {}",
            totalCount.get(), successCount.get(), failCount.get());
    }

    private void compressIfNeeded(String photoPath) {
        Path fullPath = rootPath.resolve(photoPath);

        if (!Files.exists(fullPath)) {
            log.warn("[PHOTO COMPRESS] 파일 없음: {}", fullPath);
            failCount.incrementAndGet();
            return;
        }

        try {
            long size = Files.size(fullPath);
            long limit = 1024 * 1024; // 1MB

            if (size < limit) {
                return;
            }

            String fileName = fullPath.getFileName().toString();
            String ext = fileName.substring(fileName.lastIndexOf('.') + 1);
            Path temp = fullPath.resolveSibling(fileName.replace("." + ext, "_tmp." + ext));

            Thumbnails.of(fullPath.toFile())
                .scale(0.3)
                .outputQuality(0.1)
                .toFile(temp.toFile());

            Files.move(temp, fullPath, StandardCopyOption.REPLACE_EXISTING);

            log.info("[PHOTO COMPRESS] 완료: {} ({}MB → {}MB)",
                fileName,
                String.format("%.2f", size / 1024.0 / 1024.0),
                String.format("%.2f", Files.size(fullPath) / 1024.0 / 1024.0));

            successCount.incrementAndGet();

        } catch (IOException e) {
            failCount.incrementAndGet();
            log.error("[PHOTO COMPRESS] 오류 {} => {}", photoPath, e.getMessage());
        }
    }
}
