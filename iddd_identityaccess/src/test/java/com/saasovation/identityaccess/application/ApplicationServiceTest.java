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

package com.saasovation.identityaccess.application;

import junit.framework.TestCase;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.event.EventStore;
import com.saasovation.common.persistence.CleanableStore;
import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.access.Role;
import com.saasovation.identityaccess.domain.model.identity.ContactInformation;
import com.saasovation.identityaccess.domain.model.identity.EmailAddress;
import com.saasovation.identityaccess.domain.model.identity.Enablement;
import com.saasovation.identityaccess.domain.model.identity.FullName;
import com.saasovation.identityaccess.domain.model.identity.Group;
import com.saasovation.identityaccess.domain.model.identity.Person;
import com.saasovation.identityaccess.domain.model.identity.PostalAddress;
import com.saasovation.identityaccess.domain.model.identity.RegistrationInvitation;
import com.saasovation.identityaccess.domain.model.identity.Telephone;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.User;

public abstract class ApplicationServiceTest extends TestCase {

    protected static final String FIXTURE_GROUP_NAME = "Test Group";
    protected static final String FIXTURE_PASSWORD = "SecretPassword!";
    protected static final String FIXTURE_ROLE_NAME = "Test Role";
    protected static final String FIXTURE_TENANT_DESCRIPTION = "This is a test tenant.";
    protected static final String FIXTURE_TENANT_NAME = "Test Tenant";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS = "jdoe@saasovation.com";
    protected static final String FIXTURE_USER_EMAIL_ADDRESS2 = "zdoe@saasovation.com";
    protected static final String FIXTURE_USERNAME = "jdoe";
    protected static final String FIXTURE_USERNAME2 = "zdoe";

    protected Tenant activeTenant;
    protected ApplicationContext applicationContext;
    protected EventStore eventStore;

    public ApplicationServiceTest() {
        super();
    }

    protected Group group1Aggregate() {
        return this.tenantAggregate()
                   .provisionGroup(FIXTURE_GROUP_NAME + " 1", "A test group 1.");
    }

    protected Group group2Aggregate() {
        return this.tenantAggregate()
                   .provisionGroup(FIXTURE_GROUP_NAME + " 2", "A test group 2.");
    }

    protected Role roleAggregate() {
        return this.tenantAggregate()
                   .provisionRole(FIXTURE_ROLE_NAME, "A test role.", true);
    }

    protected Tenant tenantAggregate() {
        if (activeTenant == null) {

            activeTenant =
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
        }

        return activeTenant;
    }

    protected User userAggregate() {

        Tenant tenant = this.tenantAggregate();

        RegistrationInvitation invitation =
                tenant.offerRegistrationInvitation("open-ended").openEnded();

        User user =
                tenant.registerUser(
                        invitation.invitationId(),
                        "jdoe",
                        FIXTURE_PASSWORD,
                        Enablement.indefiniteEnablement(),
                        new Person(
                                tenant.tenantId(),
                                new FullName("John", "Doe"),
                                new ContactInformation(
                                        new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS),
                                        new PostalAddress(
                                                "123 Pearl Street",
                                                "Boulder",
                                                "CO",
                                                "80301",
                                                "US"),
                                        new Telephone("303-555-1210"),
                                        new Telephone("303-555-1212"))));

        return user;
    }

    protected void setUp() throws Exception {
        System.out.println(">>>>>>>>>>>>>>>>>>>> " + this.getName());

        super.setUp();

        DomainEventPublisher.instance().reset();

        applicationContext =
                new ClassPathXmlApplicationContext(
                        new String[] {
                                "applicationContext-common.xml",
                                "applicationContext-identityaccess-application.xml",
                                "applicationContext-identityaccess-test.xml"
                        });


        this.eventStore = (EventStore) applicationContext.getBean("eventStore");

        this.clean((CleanableStore) this.eventStore);
        this.clean((CleanableStore) DomainRegistry.groupRepository());
        this.clean((CleanableStore) DomainRegistry.roleRepository());
        this.clean((CleanableStore) DomainRegistry.tenantRepository());
        this.clean((CleanableStore) DomainRegistry.userRepository());
    }

    @Override
    protected void tearDown() throws Exception {
        this.clean((CleanableStore) this.eventStore);
        this.clean((CleanableStore) DomainRegistry.groupRepository());
        this.clean((CleanableStore) DomainRegistry.roleRepository());
        this.clean((CleanableStore) DomainRegistry.tenantRepository());
        this.clean((CleanableStore) DomainRegistry.userRepository());

        super.tearDown();

        System.out.println("<<<<<<<<<<<<<<<<<<<< (done)");
    }

    private void clean(CleanableStore aCleanableStore) {
        aCleanableStore.clean();
    }
}
