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

import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemId;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemType;
import com.saasovation.agilepm.domain.model.product.backlogitem.StoryPoints;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEvent;

public class ProductBacklogItemPlanned implements DomainEvent {

    private BacklogItemId backlogItemId;
    private String category;
    private int eventVersion;
    private Date occurredOn;
    private ProductId productId;
    private StoryPoints storyPoints;
    private String summary;
    private TenantId tenantId;
    private BacklogItemType type;

    public ProductBacklogItemPlanned(
            TenantId aTenantId,
            ProductId aProductId,
            BacklogItemId aBacklogItemId,
            String aSummary,
            String aCategory,
            BacklogItemType aType,
            StoryPoints aStoryPoints) {

        super();

        this.backlogItemId = aBacklogItemId;
        this.category = aCategory;
        this.eventVersion = 1;
        this.occurredOn = new Date();
        this.productId = aProductId;
        this.storyPoints = aStoryPoints;
        this.summary = aSummary;
        this.tenantId = aTenantId;
        this.type = aType;
    }

    public BacklogItemId backlogItemId() {
        return this.backlogItemId;
    }

    public String category() {
        return this.category;
    }

    @Override
    public int eventVersion() {
        return this.eventVersion;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }

    public ProductId productId() {
        return this.productId;
    }

    public StoryPoints storyPoints() {
        return this.storyPoints;
    }

    public String summary() {
        return this.summary;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public BacklogItemType type() {
        return this.type;
    }
}
