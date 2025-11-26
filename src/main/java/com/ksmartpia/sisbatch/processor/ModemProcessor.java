package com.ksmartpia.sisbatch.processor;

import com.ksmartpia.sisbatch.model.ModemEquip;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class ModemProcessor implements ItemProcessor<ModemEquip, ModemEquip> {
    @Override
    public ModemEquip process(ModemEquip item) {
        return item; // 그대로 전달
    }
}
