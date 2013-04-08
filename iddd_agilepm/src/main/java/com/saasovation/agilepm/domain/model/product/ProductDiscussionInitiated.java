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

import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;

public class ProductDiscussionInitiated implements DomainEvent {

    private int eventVersion;
    private Date occurredOn;
    private ProductDiscussion productDiscussion;
    private ProductId productId;
    private TenantId tenantId;

    public ProductDiscussionInitiated(
            TenantId aTenantId,
            ProductId aProductId,
            ProductDiscussion aProductDiscussion) {

        super();

        this.eventVersion = 1;
        this.occurredOn = new Date();
        this.productDiscussion = aProductDiscussion;
        this.productId = aProductId;
        this.tenantId = aTenantId;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public ProductDiscussion productDiscussion() {
        return this.productDiscussion;
    }

    public ProductId productId() {
        return this.productId;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }
}
