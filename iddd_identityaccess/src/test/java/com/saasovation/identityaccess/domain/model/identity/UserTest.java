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

public class UserTest extends IdentityAccessTest {

    private boolean handled;

    public UserTest() {
        super();
    }

    public void testUserEnablementEnabled() throws Exception {

        User user = this.userAggregate();

        assertTrue(user.isEnabled());
    }

    public void testUserEnablementDisabled() throws Exception {

        final User user = this.userAggregate();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                public void handleEvent(UserEnablementChanged aDomainEvent) {
                    assertEquals(aDomainEvent.username(), user.username());
                    handled = true;
                }
                public Class<UserEnablementChanged> subscribedToEventType() {
                    return UserEnablementChanged.class;
                }
            });

        user.defineEnablement(new Enablement(false, null, null));

        assertFalse(user.isEnabled());
        assertTrue(handled);
    }

    public void testUserEnablementWithinStartEndDates() throws Exception {

        final User user = this.userAggregate();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                public void handleEvent(UserEnablementChanged aDomainEvent) {
                    assertEquals(aDomainEvent.username(), user.username());
                    handled = true;
                }
                public Class<UserEnablementChanged> subscribedToEventType() {
                    return UserEnablementChanged.class;
                }
            });

        user.defineEnablement(
                new Enablement(
                        true,
                        this.today(),
                        this.tomorrow()));

        assertTrue(user.isEnabled());
        assertTrue(handled);
    }

    public void testUserEnablementOutsideStartEndDates() throws Exception {

        final User user = this.userAggregate();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                public void handleEvent(UserEnablementChanged aDomainEvent) {
                    assertEquals(aDomainEvent.username(), user.username());
                    handled = true;
                }
                public Class<UserEnablementChanged> subscribedToEventType() {
                    return UserEnablementChanged.class;
                }
            });

        user.defineEnablement(
                new Enablement(
                        true,
                        this.dayBeforeYesterday(),
                        this.yesterday()));

        assertFalse(user.isEnabled());
        assertTrue(handled);
    }

    public void testUserEnablementUnsequencedDates() throws Exception {

        final User user = this.userAggregate();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<UserEnablementChanged>() {
                public void handleEvent(UserEnablementChanged aDomainEvent) {
                    assertEquals(aDomainEvent.username(), user.username());
                    handled = true;
                }
                public Class<UserEnablementChanged> subscribedToEventType() {
                    return UserEnablementChanged.class;
                }
            });

        boolean failure = false;

        try {
            user.defineEnablement(
                    new Enablement(
                            true,
                            this.tomorrow(),
                            this.today()));
        } catch (Throwable t) {
            failure = true;
        }

        assertTrue(failure);
        assertFalse(handled);
    }

    public void testUserDescriptor() throws Exception {

        User user = this.userAggregate();

        UserDescriptor userDescriptor =
            user.userDescriptor();

        assertNotNull(userDescriptor.emailAddress());
        assertEquals(userDescriptor.emailAddress(), FIXTURE_USER_EMAIL_ADDRESS);

        assertNotNull(userDescriptor.tenantId());
        assertEquals(userDescriptor.tenantId(), user.tenantId());

        assertNotNull(userDescriptor.username());
        assertEquals(userDescriptor.username(), FIXTURE_USERNAME);
    }

    public void testUserChangePassword() throws Exception {

        final User user = this.userAggregate();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<UserPasswordChanged>() {
                public void handleEvent(UserPasswordChanged aDomainEvent) {
                    assertEquals(aDomainEvent.username(), user.username());
                    assertEquals(aDomainEvent.tenantId(), user.tenantId());
                    handled = true;
                }
                public Class<UserPasswordChanged> subscribedToEventType() {
                    return UserPasswordChanged.class;
                }
            });

        user.changePassword(FIXTURE_PASSWORD, "ThisIsANewPassword.");

        assertTrue(handled);
    }

    public void testUserChangePasswordFails() throws Exception {

        User user = this.userAggregate();

        try {

            user.changePassword("no clue", "ThisIsANewP4ssw0rd.");

            assertEquals(FIXTURE_PASSWORD, "no clue");

        } catch (Exception e) {
            // good path, fall through
        }
    }

    public void testUserPasswordHashedOnConstruction() throws Exception {

        User user = this.userAggregate();

        assertFalse(FIXTURE_PASSWORD.equals(user.password()));
    }

    public void testUserPasswordHashedOnChange() throws Exception {

        User user = this.userAggregate();

        String strongPassword = DomainRegistry.passwordService().generateStrongPassword();

        user.changePassword(FIXTURE_PASSWORD, strongPassword);

        assertFalse(FIXTURE_PASSWORD.equals(user.password()));
        assertFalse(strongPassword.equals(user.password()));
    }

    public void testUserPersonalContactInformationChanged() throws Exception {

        final User user = this.userAggregate();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<PersonContactInformationChanged>() {
                public void handleEvent(PersonContactInformationChanged aDomainEvent) {
                    assertEquals(aDomainEvent.username(), user.username());
                    handled = true;
                }
                public Class<PersonContactInformationChanged> subscribedToEventType() {
                    return PersonContactInformationChanged.class;
                }
            });

        user.changePersonalContactInformation(
                new ContactInformation(
                    new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2),
                    new PostalAddress(
                            "123 Mockingbird Lane",
                            "Boulder",
                            "CO",
                            "80301",
                            "US"),
                    new Telephone("303-555-1210"),
                    new Telephone("303-555-1212")));

        assertEquals(new EmailAddress(FIXTURE_USER_EMAIL_ADDRESS2), user.person().emailAddress());
        assertEquals("123 Mockingbird Lane", user.person().contactInformation().postalAddress().streetAddress());
        assertTrue(handled);
    }

    public void testUserPersonNameChanged() throws Exception {

        final User user = this.userAggregate();

        DomainEventPublisher
            .instance()
            .subscribe(new DomainEventSubscriber<PersonNameChanged>() {
                public void handleEvent(PersonNameChanged aDomainEvent) {
                    assertEquals(aDomainEvent.username(), user.username());
                    assertEquals(aDomainEvent.name().firstName(), "Joe");
                    assertEquals(aDomainEvent.name().lastName(), "Smith");
                    handled = true;
                }
                public Class<PersonNameChanged> subscribedToEventType() {
                    return PersonNameChanged.class;
                }
            });

        user.changePersonalName(new FullName("Joe", "Smith"));

        assertTrue(handled);
    }
}
