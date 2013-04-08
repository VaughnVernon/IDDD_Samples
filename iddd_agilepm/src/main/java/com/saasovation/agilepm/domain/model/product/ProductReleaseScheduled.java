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

package com.saasovation.agilepm.domain.model.product;

import java.util.Date;

import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;

public class ProductReleaseScheduled implements DomainEvent {

    private Date begins;
    private String description;
    private int eventVersion;
    private Date ends;
    private String name;
    private Date occurredOn;
    private ProductId productId;
    private ReleaseId releaseId;
    private TenantId tenantId;

    public ProductReleaseScheduled(
            TenantId aTenantId,
            ProductId aProductId,
            ReleaseId aReleaseId,
            String aName,
            String aDescription,
            Date aBegins,
            Date anEnds) {

        super();

        this.begins = aBegins;
        this.description = aDescription;
        this.eventVersion = 1;
        this.ends = anEnds;
        this.name = aName;
        this.occurredOn = new Date();
        this.productId = aProductId;
        this.releaseId = aReleaseId;
        this.tenantId = aTenantId;
    }

    public Date begins() {
        return this.begins;
    }

    public String description() {
        return this.description;
    }

    public Date ends() {
        return this.ends;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    public String name() {
        return this.name;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public ProductId productId() {
        return this.productId;
    }

    public ReleaseId releaseId() {
        return this.releaseId;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }
}
