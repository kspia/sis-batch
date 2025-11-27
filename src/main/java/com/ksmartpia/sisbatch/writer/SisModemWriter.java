package com.ksmartpia.sisbatch.writer;

import com.ksmartpia.sisbatch.model.ModemEquip;
import javax.sql.DataSource;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SisModemWriter {

    @Bean
    public JdbcBatchItemWriter<ModemEquip> modemEquipWriter(
        @Qualifier("dataSource") DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<ModemEquip>()
            .sql("""
                    INSERT INTO sis_modem_equip (
                        imei,
                        modem_id,
                        imsi,
                        entity_id
                    ) VALUES (
                        :imei,
                        :modemId,
                        :imsi,
                        :entityId
                    )
                    ON CONFLICT (imei) DO UPDATE SET
                        modem_id = EXCLUDED.modem_id,
                        imsi = EXCLUDED.imsi,
                        entity_id = EXCLUDED.entity_id
                """)
            .beanMapped()
            .dataSource(dataSource)
            .build();
    }
}
