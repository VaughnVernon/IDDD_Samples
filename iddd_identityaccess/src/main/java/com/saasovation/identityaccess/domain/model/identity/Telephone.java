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

package com.saasovation.identityaccess.domain.model.identity;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.saasovation.common.AssertionConcern;

public final class Telephone extends AssertionConcern implements Serializable {

    private static final long serialVersionUID = 1L;

    private String number;

    public Telephone(String aNumber) {
        this();

        this.setNumber(aNumber);
    }

    public Telephone(Telephone aTelephone) {
        this(aTelephone.number());
    }

    public String number() {
        return this.number;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Telephone typedObject = (Telephone) anObject;
            equalObjects = this.number().equals(typedObject.number());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (35137 * 239)
            + this.number().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Telephone [number=" + number + "]";
    }

    public Telephone() {
        super();
    }

    private void setNumber(String aNumber) {
        this.assertArgumentNotEmpty(aNumber, "Telephone number is required.");
        this.assertArgumentLength(aNumber, 5, 20, "Telephone number may not be more than 20 characters.");
        this.assertArgumentTrue(
                Pattern.matches("((\\(\\d{3}\\))|(\\d{3}-))\\d{3}-\\d{4}", aNumber),
                "Telephone number or its format is invalid.");

        this.number = aNumber;
    }
}
