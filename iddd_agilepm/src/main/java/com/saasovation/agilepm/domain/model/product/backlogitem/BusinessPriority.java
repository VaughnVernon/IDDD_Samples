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

import com.saasovation.agilepm.domain.model.ValueObject;

public class BusinessPriority extends ValueObject {

    private BusinessPriorityRatings ratings;

    public BusinessPriority(BusinessPriorityRatings aBusinessPriorityRatings) {
        this();

        this.setRatings(aBusinessPriorityRatings);
    }

    public BusinessPriority(BusinessPriority aBusinessPriority) {
        this(new BusinessPriorityRatings(aBusinessPriority.ratings()));
    }

    public float costPercentage(BusinessPriorityTotals aTotals) {
        return (float) 100 * this.ratings().cost() / aTotals.totalCost();
    }

    public float priority(BusinessPriorityTotals aTotals) {
        float costAndRisk = this.costPercentage(aTotals) + this.riskPercentage(aTotals);

        return this.valuePercentage(aTotals) / costAndRisk;
    }

    public float riskPercentage(BusinessPriorityTotals aTotals) {
        return (float) 100 * this.ratings().risk() / aTotals.totalRisk();
    }

    public float totalValue() {
        return this.ratings().benefit() + this.ratings().penalty();
    }

    public float valuePercentage(BusinessPriorityTotals aTotals) {
        return (float) 100 * this.totalValue() / aTotals.totalValue();
    }

    public BusinessPriorityRatings ratings() {
        return this.ratings;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            BusinessPriority typedObject = (BusinessPriority) anObject;
            equalObjects = this.ratings().equals(typedObject.ratings());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (15681 * 13)
            + this.ratings().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "BusinessPriority [ratings=" + ratings + "]";
    }

    private BusinessPriority() {
        super();
    }

    private void setRatings(BusinessPriorityRatings aRatings) {
        this.assertArgumentNotNull(aRatings, "The ratings must be provided.");

        this.ratings = aRatings;
    }
}
