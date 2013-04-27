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

public class BusinessPriorityRatings extends ValueObject {

    private int benefit;
    private int cost;
    private int penalty;
    private int risk;

    public BusinessPriorityRatings(int aBenefit, int aPenalty, int aCost, int aRisk) {
        this();

        this.setBenefit(aBenefit);
        this.setCost(aCost);
        this.setPenalty(aPenalty);
        this.setRisk(aRisk);
    }

    public BusinessPriorityRatings(BusinessPriorityRatings aBusinessPriorityRatings) {
        this(aBusinessPriorityRatings.benefit(),
             aBusinessPriorityRatings.penalty(),
             aBusinessPriorityRatings.cost(),
             aBusinessPriorityRatings.risk());
    }

    public BusinessPriorityRatings withAdjustedBenefit(int aBenefit) {
        return new BusinessPriorityRatings(aBenefit, this.penalty(), this.cost(), this.risk());
    }

    public BusinessPriorityRatings withAdjustedCost(int aCost) {
        return new BusinessPriorityRatings(this.benefit(), this.penalty(), aCost, this.risk());
    }

    public BusinessPriorityRatings withAdjustedPenalty(int aPenalty) {
        return new BusinessPriorityRatings(this.benefit(), aPenalty, this.cost(), this.risk());
    }

    public BusinessPriorityRatings withAdjustedRisk(int aRisk) {
        return new BusinessPriorityRatings(this.benefit(), this.penalty(), this.cost(), aRisk);
    }

    public int benefit() {
        return this.benefit;
    }

    public int cost() {
        return this.cost;
    }

    public int penalty() {
        return this.penalty;
    }

    public int risk() {
        return this.risk;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            BusinessPriorityRatings typedObject = (BusinessPriorityRatings) anObject;
            equalObjects =
                this.benefit() == typedObject.benefit() &&
                this.penalty() == typedObject.penalty() &&
                this.cost() == typedObject.cost() &&
                this.risk() == typedObject.risk();
        }
        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (93263 * 17)
            + this.benefit()
            + this.penalty()
            + this.cost()
            + this.risk();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "BusinessPriorityRatings [benefit=" + benefit + ", cost=" + cost + ", penalty=" + penalty + ", risk=" + risk + "]";
    }

    private BusinessPriorityRatings() {
        super();
    }

    private void setBenefit(int aBenefit) {
        this.assertArgumentRange(aBenefit, 1, 9, "Relative benefit must be between 1 and 9.");

        this.benefit = aBenefit;
    }

    private void setCost(int aCost) {
        this.assertArgumentRange(aCost, 1, 9, "Relative cost must be between 1 and 9.");

        this.cost = aCost;
    }

    private void setPenalty(int aPenalty) {
        this.assertArgumentRange(aPenalty, 1, 9, "Relative penalty must be between 1 and 9.");

        this.penalty = aPenalty;
    }

    private void setRisk(int aRisk) {
        this.assertArgumentRange(aRisk, 1, 9, "Relative risk must be between 1 and 9.");

        this.risk = aRisk;
    }
}
