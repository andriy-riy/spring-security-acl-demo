package com.rio.dto;

import java.io.Serializable;

public record AddPermissionDTO(
        ObjectIdentityDTO objectIdentity,
        Long userId,
        Integer permission,
        boolean granting
) {

    public record ObjectIdentityDTO(
            String type,
            Serializable id
    ) {
    }
}
