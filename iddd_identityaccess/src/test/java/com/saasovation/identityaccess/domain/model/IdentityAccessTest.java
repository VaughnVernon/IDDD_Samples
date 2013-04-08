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

package com.saasovation.identityaccess.domain.model;

import java.util.Date;

import com.saasovation.identityaccess.domain.model.identity.ContactInformation;
import com.saasovation.identityaccess.domain.model.identity.EmailAddress;
import com.saasovation.identityaccess.domain.model.identity.Enablement;
import com.saasovation.identityaccess.domain.model.identity.FullName;
import com.saasovation.identityaccess.domain.model.identity.Person;
import com.saasovation.identityaccess.domain.model.identity.PostalAddress;
import com.saasovation.identityaccess.domain.model.identity.RegistrationInvitation;
import com.saasovation.identityaccess.domain.model.identity.Telephone;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.TenantId;
import com.saasovation.identityaccess.domain.model.identity.User;

public abstract class IdentityAccessTest extends DomainTest {

    protected static final String FIXTURE_PASSWORD = "SecretPassword!";
    protected static final String FIXTURE_TENANT_DESCRIPTION = "This is a test tenant.";
    protected static final String FIXTURE_TENANT_NAME = "Test Tenant";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS = "jdoe@saasovation.com";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS2 = "zdoe@saasovation.com";
    protected static final String FIXTURE_USERNAME = "jdoe";
    protected static final String FIXTURE_USERNAME2 = "zdoe";
    protected static final long TWENTY_FOUR_HOURS = (1000L * 60L * 60L * 24L);

    private Tenant tenant;

    public IdentityAccessTest() {
        super();
    }

    protected ContactInformation contactInformation() {
        return
            new ContactInformation(
                    new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS),
                    new PostalAddress(
                            "123 Pearl Street",
                            "Boulder",
                            "CO",
                            "80301",
                            "US"),
                    new Telephone("303-555-1210"),
                    new Telephone("303-555-1212"));
    }

    protected Date dayAfterTomorrow() {
        return new Date(this.today().getTime() + (TWENTY_FOUR_HOURS * 2));
    }

    protected Date dayBeforeYesterday() {
        return new Date(this.today().getTime() - (TWENTY_FOUR_HOURS * 2));
    }

    protected Person personEntity(Tenant aTenant) {

        Person person =
            new Person(
                    aTenant.tenantId(),
                    new FullName("John", "Doe"),
                    this.contactInformation());

        return person;
    }

    protected Person personEntity2(Tenant aTenant) {

        Person person =
            new Person(
                    aTenant.tenantId(),
                    new FullName("Zoe", "Doe"),
                    new ContactInformation(
                            new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2),
                            new PostalAddress(
                                    "123 Pearl Street",
                                    "Boulder",
                                    "CO",
                                    "80301",
                                    "US"),
                            new Telephone("303-555-1210"),
                            new Telephone("303-555-1212")));

        return person;
    }

    protected RegistrationInvitation registrationInvitationEntity(Tenant aTenant) throws Exception {

        Date today = new Date();

        Date tomorrow = new Date(today.getTime() + TWENTY_FOUR_HOURS);

        RegistrationInvitation registrationInvitation =
            aTenant.offerRegistrationInvitation("Today-and-Tomorrow: " + System.nanoTime())
            .startingOn(today)
            .until(tomorrow);

        return registrationInvitation;
    }

    protected Tenant tenantAggregate() {

        if (this.tenant == null) {
            TenantId tenantId =
                DomainRegistry.tenantRepository().nextIdentity();

            this.tenant =
                new Tenant(
                        tenantId,
                        FIXTURE_TENANT_NAME,
                        FIXTURE_TENANT_DESCRIPTION,
                        true);

            DomainRegistry.tenantRepository().add(tenant);
        }

        return this.tenant;
    }

    protected Date today() {
        return new Date();
    }

    protected Date tomorrow() {
        return new Date(this.today().getTime() + TWENTY_FOUR_HOURS);
    }

    protected User userAggregate() throws Exception {
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

        return user;
    }

    protected User userAggregate2() throws Exception {
        Tenant tenant = this.tenantAggregate();

        RegistrationInvitation registrationInvitation =
            this.registrationInvitationEntity(tenant);

        User user =
            tenant.registerUser(
                    registrationInvitation.invitationId(),
                    FIXTURE_USERNAME2,
                    FIXTURE_PASSWORD,
                    new Enablement(true, null, null),
                    this.personEntity2(tenant));

        return user;
    }

    protected Date yesterday() {
        return new Date(this.today().getTime() - TWENTY_FOUR_HOURS);
    }
}
