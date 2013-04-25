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

import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;

public class ProductDiscussionInitiated extends DomainEvent {

    private ProductDiscussion productDiscussion;
    private ProductId productId;
    private TenantId tenantId;

    public ProductDiscussionInitiated(
	    TenantId aTenantId,
	    ProductId aProductId,
	    ProductDiscussion aProductDiscussion) {

	super();

	this.productDiscussion = aProductDiscussion;
	this.productId = aProductId;
	this.tenantId = aTenantId;
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
