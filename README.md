# sis-batch
설치시스템(SIS) 배치 프로젝트

---

# SIS Batch – DMS → SIS 모뎀 장비 동기화 배치

## 📌 개요

- **Source DB:** `dms_modem_equip` (DataSource: `dataSource`)
- **Target DB:** `sis_modem_equip` (DataSource: `sisDataSource`)
- **동기화 구조:**
  - 최초 실행 → **Full Load (전체 약 40만 건)**
  - 이후 실행 → **Delta Load (`last_modified_date` 기준)**

### 동기화 기준

- Upsert 기준 컬럼: **`imei`**
- PostgreSQL `ON CONFLICT (imei)` 기반 insert/update 처리

---

## 📂 프로젝트 구조

```text
com.ksmartpia.sisbatch
├─ SisBatchApplication.java          ← @EnableScheduling 포함
├─ config
│   ├─ BatchConfig.java              ← 두 개의 DataSource 구성
│   └─ ModemSyncJobConfig.java       ← Job / Step 정의
├─ dao
│   └─ SyncMetaRepository.java       ← lastSyncTime 조회/저장
├─ listeners
│   ├─ StepLoggingListener.java      ← Step 통계 로그
│   ├─ JobLoggingListener.java       ← Job 시작/종료 로그
│   └─ SyncTimeUpdateListener.java   ← lastSyncTime 갱신
├─ model
│   └─ ModemEquip.java
├─ processor
│   └─ ModemProcessor.java
├─ reader
│   └─ DmsModemReader.java
├─ scheduler
│   └─ SyncScheduler.java            ← cron 실행
└─ writer
    └─ SisModemWriter.java           ← IMEI 기반 upsert
```

---

## 🔄 동기화 동작 흐름

1. Scheduler 에서 `batch_sync_meta`에서 `lastSyncTime` 조회  
2. `lastSyncTime = null` → **최초 실행, Full Load**  
3. `lastSyncTime != null` → **Delta Load**  
4. Reader가 dms_modem_equip 에서 데이터 로드  
5. Writer가 sis_modem_equip 에 Upsert  
6. Job이 COMPLETED 상태이면 SyncTimeUpdateListener가 `batch_sync_meta.last_sync_time` 업데이트  

---

## 🕒 스케줄 설정

현재 설정(테스트용):

```java
@Scheduled(cron = "0 8 14 * * *")  // 매일 14:08
```

운영용 (매일 새벽 3시):

```java
@Scheduled(cron = "0 0 3 * * *")
```

---

## 📊 로그 예시

### Step 로그

```
[STEP START] modemSyncStep
[STEP END] modemSyncStep
  READ    : 42000
  WRITE   : 42000
  SKIP    : 0
  COMMITS : 21
```

### Job 로그

```
===== MODEM SYNC JOB START =====
===== MODEM SYNC JOB END ===== Status = COMPLETED
```

### lastSyncTime 저장 로그

```
[SyncTimeUpdate] Update lastSyncTime=2025-11-26T14:08:05.123
```

---

## 🗄️ 필요 DB 테이블

### batch_sync_meta

```sql
CREATE TABLE batch_sync_meta (
    job_name       VARCHAR PRIMARY KEY,
    last_sync_time TIMESTAMP
);
```

### sis_modem_equip

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
