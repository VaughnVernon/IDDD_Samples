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

public final class EmailAddress extends AssertionConcern implements Serializable {

    private static final long serialVersionUID = 1L;

    private String address;

    public EmailAddress(String anAddress) {
        super();

        this.setAddress(anAddress);
    }

    public EmailAddress(EmailAddress anEmailAddress) {
        this(anEmailAddress.address());
    }

    public String address() {
        return this.address;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            EmailAddress typedObject = (EmailAddress) anObject;
            equalObjects = this.address().equals(typedObject.address());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (17861 * 179)
            + this.address().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "EmailAddress [address=" + address + "]";
    }

    protected EmailAddress() {
        super();
    }

    private void setAddress(String anAddress) {
        this.assertArgumentNotEmpty(anAddress, "The email address is required.");
        this.assertArgumentLength(anAddress, 1, 100, "Email address must be 100 characters or less.");
        this.assertArgumentTrue(
                Pattern.matches("\\w+([-+.']\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*", anAddress),
                "Email address format is invalid.");

        this.address = anAddress;
    }
}
