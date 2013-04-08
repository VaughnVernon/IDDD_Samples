//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.saasovation.identityaccess.infrastructure.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.saasovation.common.persistence.CleanableStore;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.TenantId;
import com.saasovation.identityaccess.domain.model.identity.TenantRepository;

public class InMemoryTenantRepository implements TenantRepository, CleanableStore {

    private Map<String,Tenant> repository;

    public InMemoryTenantRepository() {
        super();

        this.repository = new HashMap<String,Tenant>();
    }

    @Override
    public void add(Tenant aTenant) {
        String key = this.keyOf(aTenant);

        if (this.repository().containsKey(key)) {
            throw new IllegalStateException("Duplicate key.");
        }

        this.repository().put(key, aTenant);
    }

    @Override
    public TenantId nextIdentity() {
        return new TenantId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public Tenant tenantNamed(String aName) {
        for (Tenant tenant : this.repository().values()) {
            if (tenant.name().equals(aName)) {
                return tenant;
            }
        }

        return null;
    }

    @Override
    public Tenant tenantOfId(TenantId aTenantId) {
        return this.repository().get(this.keyOf(aTenantId));
    }

    @Override
    public void remove(Tenant aTenant) {
        String key = this.keyOf(aTenant);

        this.repository().remove(key);
    }

    @Override
    public void clean() {
        this.repository().clear();
    }

    private String keyOf(TenantId aTenantId) {
        String key = aTenantId.id();

        return key;
    }

    private String keyOf(Tenant aTenant) {
        return this.keyOf(aTenant.tenantId());
    }

    private Map<String,Tenant> repository() {
        return this.repository;
    }
}
