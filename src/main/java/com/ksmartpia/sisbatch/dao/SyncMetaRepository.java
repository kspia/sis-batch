package com.ksmartpia.sisbatch.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Slf4j
@Repository
@RequiredArgsConstructor
public class SyncMetaRepository {

    private final JdbcTemplate jdbcTemplate;

    public LocalDateTime getLastSyncTime(String jobName) {
        return jdbcTemplate.query(
            "SELECT last_sync_time FROM batch_sync_meta WHERE job_name = ?",
            ps -> ps.setString(1, jobName),
            rs -> rs.next() ? rs.getTimestamp("last_sync_time").toLocalDateTime() : null
        );
    }

    public void updateLastSyncTime(String jobName, LocalDateTime time) {
        log.info("▶ [SyncMeta] update lastSyncTime. jobName={}, time={}", jobName, time);

        int updated = jdbcTemplate.update(
            "UPDATE batch_sync_meta SET last_sync_time=? WHERE job_name=?",
            time, jobName
        );

        if (updated == 0) {
            log.info("▶ [SyncMeta] no row → insert new meta row.");
            jdbcTemplate.update(
                "INSERT INTO batch_sync_meta (job_name, last_sync_time) VALUES (?, ?)",
                jobName, time
            );
        }
    }
}
