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

import java.util.Date;

import com.saasovation.common.domain.model.ConcurrencySafeEntity;

public class RegistrationInvitation extends ConcurrencySafeEntity {

    private static final long serialVersionUID = 1L;

    private String description;
    private String invitationId;
    private Date startingOn;
    private TenantId tenantId;
    private Date until;

    public String description() {
        return this.description;
    }

    public String invitationId() {
        return this.invitationId;
    }

    public boolean isAvailable() {
        boolean isAvailable = false;
        if (this.startingOn() == null && this.until() == null) {
            isAvailable = true;
        } else {
            long time = (new Date()).getTime();
            if (time >= this.startingOn().getTime() && time <= this.until().getTime()) {
                isAvailable = true;
            }
        }
        return isAvailable;
    }

    public boolean isIdentifiedBy(String anInvitationIdentifier) {
        boolean isIdentified = this.invitationId().equals(anInvitationIdentifier);
        if (!isIdentified && this.description() != null) {
            isIdentified = this.description().equals(anInvitationIdentifier);
        }
        return isIdentified;
    }

    public RegistrationInvitation openEnded() {
        this.setStartingOn(null);
        this.setUntil(null);
        return this;
    }

    public RegistrationInvitation redefineAs() {
        this.setStartingOn(null);
        this.setUntil(null);
        return this;
    }

    public Date startingOn() {
        return this.startingOn;
    }

    public RegistrationInvitation startingOn(Date aDate) {
        if (this.until() != null) {
            throw new IllegalStateException("Cannot set starting-on date after until date.");
        }

        this.setStartingOn(aDate);

        // temporary if until() properly follows, but
        // prevents illegal state if until() doesn't follow
        this.setUntil(new Date(aDate.getTime() + 86400000));

        return this;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public InvitationDescriptor toDescriptor() {
        return
                new InvitationDescriptor(
                        this.tenantId(),
                        this.invitationId(),
                        this.description(),
                        this.startingOn(),
                        this.until());
    }

    public Date until() {
        return this.until;
    }

    public RegistrationInvitation until(Date aDate) {
        if (this.startingOn() == null) {
            throw new IllegalStateException("Cannot set until date before setting starting-on date.");
        }

        this.setUntil(aDate);

        return this;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            RegistrationInvitation typedObject = (RegistrationInvitation) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.invitationId().equals(typedObject.invitationId());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (6325 * 233)
            + this.tenantId().hashCode()
            + this.invitationId().hashCode();

        return hashCodeValue;
    }


    @Override
    public String toString() {
        return "RegistrationInvitation ["
                + "tenantId=" + tenantId
                + ", description=" + description
                + ", invitationId=" + invitationId
                + ", startingOn=" + startingOn
                + ", until=" + until + "]";
    }

    protected RegistrationInvitation(
            TenantId aTenantId,
            String anInvitationId,
            String aDescription) {

        this();

        this.setDescription(aDescription);
        this.setInvitationId(anInvitationId);
        this.setTenantId(aTenantId);

        this.assertValidInvitationDates();
    }

    protected RegistrationInvitation() {
        super();
    }

    protected void assertValidInvitationDates() {
        // either both dates must be null, or both dates must be set
        if (this.startingOn() == null && this.until() == null) {
            ; // valid
        } else if (this.startingOn() == null || this.until() == null &&
                   this.startingOn() != this.until()) {
            throw new IllegalStateException("This is an invalid open-ended invitation.");
        } else if (this.startingOn().after(this.until())) {
            throw new IllegalStateException("The starting date and time must be before the until date and time.");
        }
    }

    protected void setDescription(String aDescription) {
        this.assertArgumentNotEmpty(aDescription, "The invitation description is required.");
        this.assertArgumentLength(aDescription, 1, 100, "The invitation description must be 100 characters or less.");

        this.description = aDescription;
    }

    protected void setInvitationId(String anInvitationId) {
        this.assertArgumentNotEmpty(anInvitationId, "The invitationId is required.");
        this.assertArgumentLength(anInvitationId, 1, 36, "The invitation id must be 36 characters or less.");

        this.invitationId = anInvitationId;
    }

    protected void setStartingOn(Date aStartingOn) {
        this.startingOn = aStartingOn;
    }

    protected void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenantId is required.");

        this.tenantId = aTenantId;
    }

    protected void setUntil(Date anUntil) {
        this.until = anUntil;
    }
}
