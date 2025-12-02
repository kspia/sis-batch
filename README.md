
# 📘 SIS Batch 프로젝트 README

SIS 시스템에서 운영하는 **모뎀 동기화 배치** 및 **이미지 압축 배치**의 전체 구조와 동작 방식을 명확히 정리한 문서입니다.

---

# 🚀 프로젝트 개요

본 프로젝트는 SIS 시스템 내에서 운영되는 두 가지 핵심 배치 작업을 포함합니다.

1. **📡 모뎀 정보 동기화 배치 (DMS → SIS)**
2. **🖼 사진 압축 배치 (1회성)**

각 배치는 Spring Batch + Scheduler 기반으로 구성되며, 운영 환경에서 안정적으로 반복 혹은 단발성 실행되도록 설계되었습니다.

---

# 📂 프로젝트 구조

```
com.ksmartpia.sisbatch
├─ SisBatchApplication.java
│    └─ @EnableScheduling 포함 (스케줄러 활성화)
│
├─ config
│    ├─ BatchConfig.java                → Batch 공통 설정 (DataSource, TxManager)
│    └─ ModemSyncJobConfig.java         → 모뎀 동기화 Job/Step 구성
│
├─ dao
│    └─ SyncMetaRepository.java         → lastSyncTime 조회/저장
│
├─ listeners
│    ├─ JobLoggingListener.java         → Job 시작/종료 로그 처리
│    ├─ StepLoggingListener.java        → Step 처리 건수 통계 출력
│    └─ SyncTimeUpdateListener.java     → 배치 종료 후 SyncTime 업데이트
│
├─ model
│    └─ ModemEquip.java                 → 모뎀 엔티티 모델
│
├─ processor
│    └─ ModemProcessor.java             → Reader → Writer 중간 데이터 가공
│
├─ reader
│    └─ DmsModemReader.java             → DMS DB에서 모뎀 데이터 조회
│
├─ scheduler
│    ├─ SyncScheduler.java              → 모뎀 동기화 배치 스케줄러
│    └─ PhotoCompressScheduler.java     → 2025-11-29 단 1회 실행되는 이미지 압축 스케줄러
│
├─ service
│    └─ PhotoCompressorService.java     → 이미지 압축 로직
│
└─ writer
     └─ SisModemWriter.java             → IMEI 기준 Upsert Writer (ON CONFLICT)
```

---

# 📡 모뎀 동기화 배치 (Modem Sync Batch)

## ✅ 동작 개요

1. Scheduler가 `batch_sync_meta`에서 `last_sync_time` 조회  
2. 최초 실행 또는 강제 초기화 시 → **Full Load**
3. 이후 실행 → `last_modified_date` 기준 **Delta Load**
4. Reader → Processor → Writer 순서로 처리
5. Writer는 PostgreSQL `ON CONFLICT (imei)` 기반 **Upsert**
6. Job 종료 후 `SyncTimeUpdateListener`가 최신 SyncTime 저장

## 🔄 처리 흐름

```
SyncScheduler
  → ModemSyncJob
      → Step(modemSyncStep)
          → DmsModemReader
          → ModemProcessor
          → SisModemWriter(UPSERT)
          → SyncTimeUpdateListener
```

---

# 🖼 사진 압축 배치 (Photo Compress Batch)

## ✔ 특징

- 실행시간: **2025-11-29 00:00 단 한 번**
- `sis_photo` 테이블에서 이미지 경로 조회
- 1MB 이상 파일만 압축
- 4-thread Executor 기반 병렬 처리
- Thumbnailator 사용
- 압축 후 기존 파일 교체
- 성공/실패 파일 카운트 로그 출력

## ✔ 스케줄러 예시

```java
@Scheduled(cron = "0 0 0 29 11 *")
public void runPhotoCompressBatch() {
    log.info("==== PHOTO COMPRESS BATCH START ====");
    photoCompressorService.run();
    log.info("==== PHOTO COMPRESS BATCH END ====");
}
```

> 압축 배치는 스케줄러 주석/삭제 시 다시 실행되지 않습니다.

---

# 🗄️ DB 구조

## batch_sync_meta

```sql
CREATE TABLE batch_sync_meta (
    job_name VARCHAR PRIMARY KEY,
    last_sync_time TIMESTAMP
);
```

## sis_modem_equip

```sql
CREATE TABLE sis_modem_equip (
    modem_id           VARCHAR NOT NULL,
    imei               VARCHAR NOT NULL,
    imsi               VARCHAR,
    entity_id          VARCHAR,
    created_date       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (imei)
);
```

---

# 🕒 스케줄 요약

| 작업 | 스케줄 | 비고 |
|------|--------|------|
| **모뎀 동기화 배치** | 4시간마다 실행 | Delta Load |
| **사진 압축 배치** | 2025-11-29 00:00 | 단 1회 실행 |

---

# 📌 비고 및 운영 주의사항

- 모뎀 Sync 배치는 운영 스케줄에 따라 자유롭게 조정 가능  
- 사진 압축 배치는 재실행되지 않도록 반드시 스케줄러 관리 필요  
- Step chunk size, thread pool 등은 운영 상황에 따라 조정 가능  



