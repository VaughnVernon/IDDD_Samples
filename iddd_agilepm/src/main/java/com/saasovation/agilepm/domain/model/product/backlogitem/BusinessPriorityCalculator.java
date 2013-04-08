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
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class BusinessPriorityCalculator {

    private BacklogItemRepository backlogItemRepository;

    public BusinessPriorityCalculator(
            BacklogItemRepository aBacklogItemRepository) {

        super();

        this.backlogItemRepository = aBacklogItemRepository;
    }

    public BusinessPriorityTotals businessPriorityTotals(
            TenantId aTenantId,
            ProductId aProductId) {

        int totalBenefit = 0;
        int totalPenalty = 0;
        int totalCost = 0;
        int totalRisk = 0;

        Collection<BacklogItem> outstandingBacklogItems =
                this.backlogItemRepository()
                    .allOutstandingProductBacklogItems(aTenantId, aProductId);

        for (BacklogItem backlogItem : outstandingBacklogItems) {
            if (backlogItem.hasBusinessPriority()) {
                BusinessPriorityRatings ratings =
                        backlogItem.businessPriority().ratings();

                totalBenefit += ratings.benefit();
                totalPenalty += ratings.penalty();
                totalCost += ratings.cost();
                totalRisk += ratings.risk();
            }
        }

        BusinessPriorityTotals businessPriorityTotals =
                new BusinessPriorityTotals(
                        totalBenefit,
                        totalPenalty,
                        totalBenefit + totalPenalty,
                        totalCost,
                        totalRisk);

        return businessPriorityTotals;
    }

    private BacklogItemRepository backlogItemRepository() {
        return this.backlogItemRepository;
    }
}
