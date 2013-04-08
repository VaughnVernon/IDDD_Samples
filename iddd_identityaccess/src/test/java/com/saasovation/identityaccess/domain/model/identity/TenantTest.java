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

import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.domain.model.DomainEventSubscriber;
import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.IdentityAccessTest;

public class TenantTest extends IdentityAccessTest {

    private boolean handled1;
    private boolean handled2;

    public TenantTest() {
        super();
    }

    public void testProvisionTenant() throws Exception {

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<TenantProvisioned>() {
                public void handleEvent(TenantProvisioned aDomainEvent) {
                    handled1 = true;
                }
                public Class<TenantProvisioned> subscribedToEventType() {
                    return TenantProvisioned.class;
                }
            });

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<TenantAdministratorRegistered>() {
                public void handleEvent(TenantAdministratorRegistered aDomainEvent) {
                    handled2 = true;
                }
                public Class<TenantAdministratorRegistered> subscribedToEventType() {
                    return TenantAdministratorRegistered.class;
                }
            });

        Tenant tenant =
            DomainRegistry
                .tenantProvisioningService()
                .provisionTenant(
                        FIXTURE_TENANT_NAME,
                        FIXTURE_TENANT_DESCRIPTION,
                        new FullName("John", "Doe"),
                        new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS),
                        new PostalAddress(
                                "123 Pearl Street",
                                "Boulder",
                                "CO",
                                "80301",
                                "US"),
                        new Telephone("303-555-1210"),
                        new Telephone("303-555-1212"));

        assertTrue(handled1);
        assertTrue(handled2);

        assertNotNull(tenant.tenantId());
        assertNotNull(tenant.tenantId().id());
        assertEquals(36, tenant.tenantId().id().length());
        assertEquals(FIXTURE_TENANT_NAME, tenant.name());
        assertEquals(FIXTURE_TENANT_DESCRIPTION, tenant.description());
    }

    public void testCreateOpenEndedInvitation() throws Exception {

        Tenant tenant = this.tenantAggregate();

        tenant
            .offerRegistrationInvitation("Open-Ended")
            .openEnded();

        assertNotNull(tenant.redefineRegistrationInvitationAs("Open-Ended"));
    }

    public void testOpenEndedInvitationAvailable() throws Exception {

        Tenant tenant = this.tenantAggregate();

        tenant
            .offerRegistrationInvitation("Open-Ended")
            .openEnded();

        assertTrue(tenant.isRegistrationAvailableThrough("Open-Ended"));
    }

    public void testClosedEndedInvitationAvailable() throws Exception {

        Tenant tenant = this.tenantAggregate();

        tenant
            .offerRegistrationInvitation("Today-and-Tomorrow")
            .startingOn(this.today())
            .until(this.tomorrow());

        assertTrue(tenant.isRegistrationAvailableThrough("Today-and-Tomorrow"));
    }

    public void testClosedEndedInvitationNotAvailable() throws Exception {

        Tenant tenant = this.tenantAggregate();

        tenant
            .offerRegistrationInvitation("Tomorrow-and-Day-After-Tomorrow")
            .startingOn(this.tomorrow())
            .until(this.dayAfterTomorrow());

        assertFalse(tenant.isRegistrationAvailableThrough("Tomorrow-and-Day-After-Tomorrow"));
    }

    public void testAvailableInivitationDescriptor() throws Exception {

        Tenant tenant = this.tenantAggregate();

        tenant
            .offerRegistrationInvitation("Open-Ended")
            .openEnded();

        tenant
            .offerRegistrationInvitation("Today-and-Tomorrow")
            .startingOn(this.today())
            .until(this.tomorrow());

        assertEquals(tenant.allAvailableRegistrationInvitations().size(), 2);
    }

    public void testUnavailableInivitationDescriptor() throws Exception {

        Tenant tenant = this.tenantAggregate();

        tenant
            .offerRegistrationInvitation("Tomorrow-and-Day-After-Tomorrow")
            .startingOn(this.tomorrow())
            .until(this.dayAfterTomorrow());

        assertEquals(tenant.allUnavailableRegistrationInvitations().size(), 1);
    }

    public void testRegisterUser() throws Exception {

        Tenant tenant = this.tenantAggregate();

        RegistrationInvitation registrationInvitation =
            this.registrationInvitationEntity(tenant);

        User user =
            tenant.registerUser(
                    registrationInvitation.invitationId(),
                    FIXTURE_USERNAME,
                    FIXTURE_PASSWORD,
                    new Enablement(true, null, null),
                    this.personEntity(tenant));

        assertNotNull(user);

        DomainRegistry.userRepository().add(user);

        assertNotNull(user.enablement());
        assertNotNull(user.person());
        assertNotNull(user.userDescriptor());
    }
}
