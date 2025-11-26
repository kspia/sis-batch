package com.ksmartpia.sisbatch.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ModemEquip {

    private Long modemSid;
    private String imei;
    private String imsi;
    private String entityId;
    private String modemId;
    private LocalDateTime deliveryDate;
    private String deliveryDestinationCd;
    private String modelCd;
    private String modemCompanyCd;
    private String telecomCd;
    private String serviceCode;
    private String createdBy;
    private LocalDateTime createdDate;
    private String lastModifiedBy;
    private LocalDateTime lastModifiedDate;
}

