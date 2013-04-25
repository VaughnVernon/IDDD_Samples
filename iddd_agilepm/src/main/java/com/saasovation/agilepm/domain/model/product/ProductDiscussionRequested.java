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

import com.saasovation.agilepm.domain.model.team.ProductOwnerId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;

public class ProductDiscussionRequested extends DomainEvent {

    private String description;
    private String name;
    private ProductId productId;
    private ProductOwnerId productOwnerId;
    private boolean requestingDiscussion;
    private TenantId tenantId;

    public ProductDiscussionRequested(
	    TenantId aTenantId,
	    ProductId aProductId,
	    ProductOwnerId aProductOwnerId,
	    String aName,
	    String aDescription,
	    boolean aRequestingDiscussion) {

	super();

	this.description = aDescription;
	this.name = aName;
	this.productId = aProductId;
	this.productOwnerId = aProductOwnerId;
	this.requestingDiscussion = aRequestingDiscussion;
	this.tenantId = aTenantId;
    }

    public String description() {
	return this.description;
    }

    public String name() {
	return this.name;
    }

    public ProductId productId() {
	return this.productId;
    }

    public ProductOwnerId productOwnerId() {
	return this.productOwnerId;
    }

    public boolean isRequestingDiscussion() {
	return this.requestingDiscussion;
    }

    public TenantId tenantId() {
	return this.tenantId;
    }
}
