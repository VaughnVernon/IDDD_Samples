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

import java.util.*;

import com.saasovation.common.domain.model.*;
import com.saasovation.identityaccess.domain.model.access.*;

public class Tenant extends ConcurrencySafeEntity {

    private static final long serialVersionUID = 1L;

    private boolean active;
    private String description;
    private String name;
    private Set<RegistrationInvitation> registrationInvitations;
    private TenantId tenantId;

    public Tenant(TenantId aTenantId, String aName, String aDescription, boolean anActive) {
        this();

        this.setActive(anActive);
        this.setDescription(aDescription);
        this.setName(aName);
        this.setTenantId(aTenantId);
    }

    public void activate() {
        if (!this.isActive()) {

            this.setActive(true);

            DomainEventPublisher
                .instance()
                .publish(new TenantActivated(this.tenantId()));
        }
    }

    public Collection<InvitationDescriptor> allAvailableRegistrationInvitations() {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        return this.allRegistrationInvitationsFor(true);
    }

    public Collection<InvitationDescriptor> allUnavailableRegistrationInvitations() {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        return this.allRegistrationInvitationsFor(false);
    }

    public void deactivate() {
        if (this.isActive()) {

            this.setActive(false);

            DomainEventPublisher
                .instance()
                .publish(new TenantDeactivated(this.tenantId()));
        }
    }

    public String description() {
        return this.description;
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isRegistrationAvailableThrough(String anInvitationIdentifier) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        RegistrationInvitation invitation =
            this.invitation(anInvitationIdentifier);

        return invitation == null ? false : invitation.isAvailable();
    }

    public String name() {
        return this.name;
    }

    public RegistrationInvitation offerRegistrationInvitation(String aDescription) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        this.assertStateFalse(
                this.isRegistrationAvailableThrough(aDescription),
                "Invitation already exists.");

        RegistrationInvitation invitation =
            new RegistrationInvitation(
                    this.tenantId(),
                    UUID.randomUUID().toString().toUpperCase(),
                    aDescription);

        boolean added = this.registrationInvitations().add(invitation);

        this.assertStateTrue(added, "The invitation should have been added.");

        return invitation;
    }

    public Group provisionGroup(String aName, String aDescription) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        Group group = new Group(this.tenantId(), aName, aDescription);

        DomainEventPublisher
            .instance()
            .publish(new GroupProvisioned(
                    this.tenantId(),
                    aName));

        return group;
    }

    public Role provisionRole(String aName, String aDescription) {
        return this.provisionRole(aName, aDescription, false);
    }

    public Role provisionRole(String aName, String aDescription, boolean aSupportsNesting) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        Role role = new Role(this.tenantId(), aName, aDescription, aSupportsNesting);

        DomainEventPublisher
            .instance()
            .publish(new RoleProvisioned(
                    this.tenantId(),
                    aName));

        return role;
    }

    public RegistrationInvitation redefineRegistrationInvitationAs(String anInvitationIdentifier) {
        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        RegistrationInvitation invitation =
            this.invitation(anInvitationIdentifier);

        if (invitation != null) {
            invitation.redefineAs().openEnded();
        }

        return invitation;
    }

    public User registerUser(
            String anInvitationIdentifier,
            String aUsername,
            String aPassword,
            Enablement anEnablement,
            Person aPerson) {

        this.assertStateTrue(this.isActive(), "Tenant is not active.");

        User user = null;

        if (this.isRegistrationAvailableThrough(anInvitationIdentifier)) {

            // ensure same tenant
            aPerson.setTenantId(this.tenantId());

            user = new User(
                    this.tenantId(),
                    aUsername,
                    aPassword,
                    anEnablement,
                    aPerson);
        }

        return user;
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    public void withdrawInvitation(String anInvitationIdentifier) {
        RegistrationInvitation invitation =
            this.invitation(anInvitationIdentifier);

        if (invitation != null) {
            this.registrationInvitations().remove(invitation);
        }
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Tenant typedObject = (Tenant) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.name().equals(typedObject.name());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (48123 * 257)
            + this.tenantId().hashCode()
            + this.name().hashCode();

        return hashCodeValue;
    }

    @Override
    public String toString() {
        return "Tenant [active=" + active + ", description=" + description
                + ", name=" + name + ", tenantId=" + tenantId + "]";
    }

    protected Tenant() {
        super();

        this.setRegistrationInvitations(new HashSet<RegistrationInvitation>(0));
    }

    protected void setActive(boolean anActive) {
        this.active = anActive;
    }

    protected Collection<InvitationDescriptor> allRegistrationInvitationsFor(boolean isAvailable) {
        Set<InvitationDescriptor> allInvitations = new HashSet<InvitationDescriptor>();

        for (RegistrationInvitation invitation : this.registrationInvitations()) {
            if (invitation.isAvailable() == isAvailable) {
                allInvitations.add(invitation.toDescriptor());
            }
        }

        return Collections.unmodifiableSet(allInvitations);
    }

    protected void setDescription(String aDescription) {
        this.assertArgumentNotEmpty(aDescription, "The tenant description is required.");
        this.assertArgumentLength(aDescription, 1, 100, "The tenant description must be 100 characters or less.");

        this.description = aDescription;
    }

    protected RegistrationInvitation invitation(String anInvitationIdentifier) {
        for (RegistrationInvitation invitation : this.registrationInvitations()) {
            if (invitation.isIdentifiedBy(anInvitationIdentifier)) {
                return invitation;
            }
        }

        return null;
    }

    protected void setName(String aName) {
        this.assertArgumentNotEmpty(aName, "The tenant name is required.");
        this.assertArgumentLength(aName, 1, 100, "The name must be 100 characters or less.");

        this.name = aName;
    }

    protected Set<RegistrationInvitation> registrationInvitations() {
        return this.registrationInvitations;
    }

    protected void setRegistrationInvitations(Set<RegistrationInvitation> aRegistrationInvitations) {
        this.registrationInvitations = aRegistrationInvitations;
    }

    protected void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "TenentId is required.");

        this.tenantId = aTenantId;
    }
}
