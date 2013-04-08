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

import com.saasovation.common.AssertionConcern;

public final class ContactInformation extends AssertionConcern implements Serializable {

    private static final long serialVersionUID = 1L;

    private EmailAddress emailAddress;
    private PostalAddress postalAddress;
    private Telephone primaryTelephone;
    private Telephone secondaryTelephone;

    public ContactInformation(
            EmailAddress anEmailAddress,
            PostalAddress aPostalAddress,
            Telephone aPrimaryTelephone,
            Telephone aSecondaryTelephone) {

        super();

        this.setEmailAddress(anEmailAddress);
        this.setPostalAddress(aPostalAddress);
        this.setPrimaryTelephone(aPrimaryTelephone);
        this.setSecondaryTelephone(aSecondaryTelephone);
    }

    public ContactInformation(ContactInformation aContactInformation) {
        this(aContactInformation.emailAddress(),
             aContactInformation.postalAddress(),
             aContactInformation.primaryTelephone(),
             aContactInformation.secondaryTelephone());
    }

    public ContactInformation changeEmailAddress(EmailAddress anEmailAddress) {
        return new ContactInformation(
                anEmailAddress,
                this.postalAddress(),
                this.primaryTelephone(),
                this.secondaryTelephone());
    }

    public ContactInformation changePostalAddress(PostalAddress aPostalAddress) {
        return new ContactInformation(
               this.emailAddress(),
               aPostalAddress,
               this.primaryTelephone(),
               this.secondaryTelephone());
    }

    public ContactInformation changePrimaryTelephone(Telephone aTelephone) {
        return new ContactInformation(
               this.emailAddress(),
               this.postalAddress(),
               aTelephone,
               this.secondaryTelephone());
    }

    public ContactInformation changeSecondaryTelephone(Telephone aTelephone) {
        return new ContactInformation(
               this.emailAddress(),
               this.postalAddress(),
               this.primaryTelephone(),
               aTelephone);
    }

    public EmailAddress emailAddress() {
        return this.emailAddress;
    }

    public PostalAddress postalAddress() {
        return this.postalAddress;
    }

    public Telephone primaryTelephone() {
        return this.primaryTelephone;
    }

    public Telephone secondaryTelephone() {
        return this.secondaryTelephone;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            ContactInformation typedObject = (ContactInformation) anObject;
            equalObjects =
                this.emailAddress().equals(typedObject.emailAddress()) &&
                this.postalAddress().equals(typedObject.postalAddress()) &&
                this.primaryTelephone().equals(typedObject.primaryTelephone()) &&
                ((this.secondaryTelephone() == null && typedObject.secondaryTelephone() == null) ||
                 (this.secondaryTelephone() != null && this.secondaryTelephone().equals(typedObject.secondaryTelephone())));
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (73213 * 173)
            + this.emailAddress().hashCode()
            + this.postalAddress().hashCode()
            + this.primaryTelephone().hashCode()
            + (this.secondaryTelephone() == null ? 0:this.secondaryTelephone().hashCode());

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "ContactInformation [emailAddress=" + emailAddress + ", postalAddress=" + postalAddress + ", primaryTelephone="
                + primaryTelephone + ", secondaryTelephone=" + secondaryTelephone + "]";
    }

    protected ContactInformation() {
        super();
    }

    private void setEmailAddress(EmailAddress anEmailAddress) {
        this.assertArgumentNotNull(anEmailAddress, "The email address is required.");

        this.emailAddress = anEmailAddress;
    }

    private void setPostalAddress(PostalAddress aPostalAddress) {
        this.assertArgumentNotNull(aPostalAddress, "The postal address is required.");

        this.postalAddress = aPostalAddress;
    }

    private void setPrimaryTelephone(Telephone aPrimaryTelephone) {
        this.assertArgumentNotNull(aPrimaryTelephone, "The primary telephone is required.");

        this.primaryTelephone = aPrimaryTelephone;
    }

    private void setSecondaryTelephone(Telephone aSecondaryTelephone) {
        this.secondaryTelephone = aSecondaryTelephone;
    }
}
