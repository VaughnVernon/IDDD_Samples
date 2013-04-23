package com.saasovation.issuetrack.domain.model.tenant;

import static java.util.UUID.randomUUID;

import java.util.UUID;

import com.saasovation.common.domain.model.Id;

public class TenantId extends Id {

    public static TenantId newTenantId() {
	return tenantId(randomUUID());
    }

    public static TenantId tenantId(UUID value) {
	return new TenantId(value);
    }

    private TenantId(UUID value) {
	super(value);
    }

}
