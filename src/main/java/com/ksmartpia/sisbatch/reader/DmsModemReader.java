package com.ksmartpia.sisbatch.reader;

import com.ksmartpia.sisbatch.model.ModemEquip;
import java.time.LocalDateTime;
import javax.sql.DataSource;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

@Configuration
public class DmsModemReader {

    @Bean
    @StepScope
    public JdbcCursorItemReader<ModemEquip> modemEquipReader(
        @Qualifier("dataSource") DataSource dataSource,
        @Value("#{jobParameters['lastSyncTime']}") String lastSyncTimeStr
    ) {
        LocalDateTime lastSyncTime =
            lastSyncTimeStr == null ? null : LocalDateTime.parse(lastSyncTimeStr);

        String sql = (lastSyncTime == null)
            ? """
              SELECT *
              FROM dms_modem_equip
            """
            : """
                  SELECT *
                  FROM dms_modem_equip
                  WHERE last_modified_date >= ?
                """;

        return new JdbcCursorItemReaderBuilder<ModemEquip>()
            .name("modemEquipReader")
            .dataSource(dataSource)
            .sql(sql)
            .preparedStatementSetter(ps -> {
                if (lastSyncTime != null) {
                    ps.setObject(1, lastSyncTime);
                }
            })
            .rowMapper(new BeanPropertyRowMapper<>(ModemEquip.class))
            .fetchSize(2000)
            .build();
    }

}
