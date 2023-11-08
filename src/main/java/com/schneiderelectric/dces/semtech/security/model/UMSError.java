package com.schneiderelectric.dces.semtech.security.model;

import lombok.Data;

@Data
public class UMSError {

    private int status;
    private String responseStatus;

    private String developerMessage;

}
