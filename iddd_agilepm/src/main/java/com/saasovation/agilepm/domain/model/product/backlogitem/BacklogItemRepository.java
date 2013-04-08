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

package com.saasovation.agilepm.domain.model.product.backlogitem;

import java.util.Collection;

import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.product.sprint.SprintId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public interface BacklogItemRepository {

    public Collection<BacklogItem> allBacklogItemsComittedTo(TenantId aTenantId, SprintId aSprintId);

    public Collection<BacklogItem> allBacklogItemsScheduledFor(TenantId aTenantId, ReleaseId aReleaseId);

    public Collection<BacklogItem> allOutstandingProductBacklogItems(TenantId aTenantId, ProductId aProductId);

    public Collection<BacklogItem> allProductBacklogItems(TenantId aTenantId, ProductId aProductId);

    public BacklogItem backlogItemOfId(TenantId aTenantId, BacklogItemId aBacklogItemId);

    public BacklogItemId nextIdentity();

    public void remove(BacklogItem aBacklogItem);

    public void removeAll(Collection<BacklogItem> aBacklogItemCollection);

    public void save(BacklogItem aBacklogItem);

    public void saveAll(Collection<BacklogItem> aBacklogItemCollection);
}
