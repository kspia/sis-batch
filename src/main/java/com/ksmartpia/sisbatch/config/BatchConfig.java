package com.ksmartpia.sisbatch.config;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    // Batch 메타데이터 + dms_modem_equip 읽기용 (기본 DataSource)
    @Bean(name = "dataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.dms")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }

    // sis_modem_equip 쓰기용
    @Bean(name = "sisDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sis")
    public DataSource sisDataSource() {
        return DataSourceBuilder.create().build();
    }
}
