package com.ksmartpia.sisbatch.config;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    // SIS DB를 기본(Primary) DataSource로 지정
    @Primary
    @Bean(name = "dataSource")
    @ConfigurationProperties(prefix = "spring.datasource.sis")
    public DataSource sisDataSource() {
        return DataSourceBuilder.create().build();
    }

    // DMS DB는 보조로 사용 (Reader에서 명시적으로 사용)
    @Bean(name = "dmsDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.dms")
    public DataSource dmsDataSource() {
        return DataSourceBuilder.create().build();
    }

    // Primary 기반 JdbcTemplate (SIS DB 사용)
    @Primary
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("dataSource") DataSource ds) {
        return new JdbcTemplate(ds);
    }
}
