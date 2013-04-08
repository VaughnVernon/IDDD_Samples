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

import java.util.Date;

import com.saasovation.identityaccess.application.command.ActivateTenantCommand;
import com.saasovation.identityaccess.application.command.AddGroupToGroupCommand;
import com.saasovation.identityaccess.application.command.AddUserToGroupCommand;
import com.saasovation.identityaccess.application.command.AuthenticateUserCommand;
import com.saasovation.identityaccess.application.command.ChangeContactInfoCommand;
import com.saasovation.identityaccess.application.command.ChangeEmailAddressCommand;
import com.saasovation.identityaccess.application.command.ChangePostalAddressCommand;
import com.saasovation.identityaccess.application.command.ChangePrimaryTelephoneCommand;
import com.saasovation.identityaccess.application.command.ChangeSecondaryTelephoneCommand;
import com.saasovation.identityaccess.application.command.ChangeUserPasswordCommand;
import com.saasovation.identityaccess.application.command.ChangeUserPersonalNameCommand;
import com.saasovation.identityaccess.application.command.DeactivateTenantCommand;
import com.saasovation.identityaccess.application.command.DefineUserEnablementCommand;
import com.saasovation.identityaccess.application.command.RemoveGroupFromGroupCommand;
import com.saasovation.identityaccess.application.command.RemoveUserFromGroupCommand;
import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.identity.Group;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.User;
import com.saasovation.identityaccess.domain.model.identity.UserDescriptor;

public class IdentityApplicationServiceTest extends ApplicationServiceTest {

    public IdentityApplicationServiceTest() {
        super();
    }

    public void testActivateTenant() throws Exception {
        Tenant tenant = this.tenantAggregate();
        tenant.deactivate();
        assertFalse(tenant.isActive());

        ApplicationServiceRegistry
            .identityApplicationService()
            .activateTenant(new ActivateTenantCommand(tenant.tenantId().id()));

        Tenant changedTenant = DomainRegistry.tenantRepository().tenantOfId(tenant.tenantId());

        assertNotNull(changedTenant);
        assertEquals(tenant.name(), changedTenant.name());
        assertTrue(changedTenant.isActive());
    }

    public void testAddGroupToGroup() throws Exception {
        Group parentGroup = this.group1Aggregate();
        DomainRegistry.groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        DomainRegistry.groupRepository().add(childGroup);

        assertEquals(0, parentGroup.groupMembers().size());

        ApplicationServiceRegistry
            .identityApplicationService()
            .addGroupToGroup(new AddGroupToGroupCommand(
                    parentGroup.tenantId().id(),
                    parentGroup.name(),
                    childGroup.name()));

        assertEquals(1, parentGroup.groupMembers().size());
    }

    public void testAddUserToGroup() throws Exception {
        Group parentGroup = this.group1Aggregate();
        DomainRegistry.groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        DomainRegistry.groupRepository().add(childGroup);

        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        assertEquals(0, parentGroup.groupMembers().size());
        assertEquals(0, childGroup.groupMembers().size());

        parentGroup.addGroup(childGroup, DomainRegistry.groupMemberService());

        ApplicationServiceRegistry
            .identityApplicationService()
            .addUserToGroup(new AddUserToGroupCommand(
                childGroup.tenantId().id(),
                childGroup.name(),
                user.username()));

        assertEquals(1, parentGroup.groupMembers().size());
        assertEquals(1, childGroup.groupMembers().size());
        assertTrue(parentGroup.isMember(user, DomainRegistry.groupMemberService()));
        assertTrue(childGroup.isMember(user, DomainRegistry.groupMemberService()));
    }

    public void testAuthenticateUser() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        UserDescriptor userDescriptor =
                ApplicationServiceRegistry
                    .identityApplicationService()
                    .authenticateUser(new AuthenticateUserCommand(
                            user.tenantId().id(),
                            user.username(),
                            FIXTURE_PASSWORD));

        assertNotNull(userDescriptor);
        assertEquals(user.username(), userDescriptor.username());
    }

    public void testDeactivateTenant() throws Exception {
        Tenant tenant = this.tenantAggregate();
        assertTrue(tenant.isActive());

        ApplicationServiceRegistry
            .identityApplicationService()
            .deactivateTenant(new DeactivateTenantCommand(tenant.tenantId().id()));

        Tenant changedTenant = DomainRegistry.tenantRepository().tenantOfId(tenant.tenantId());

        assertNotNull(changedTenant);
        assertEquals(tenant.name(), changedTenant.name());
        assertFalse(changedTenant.isActive());
    }

    public void testChangeUserContactInformation() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        ApplicationServiceRegistry
            .identityApplicationService()
            .changeUserContactInformation(
                    new ChangeContactInfoCommand(
                            user.tenantId().id(),
                            user.username(),
                            "mynewemailaddress@saasovation.com",
                            "777-555-1211",
                            "777-555-1212",
                            "123 Pine Street",
                            "Loveland",
                            "CO",
                            "80771",
                            "US"));

        User changedUser =
                DomainRegistry
                    .userRepository()
                    .userWithUsername(
                            user.tenantId(),
                            user.username());

        assertNotNull(changedUser);
        assertEquals("mynewemailaddress@saasovation.com", changedUser.person().emailAddress().address());
        assertEquals("777-555-1211", changedUser.person().contactInformation().primaryTelephone().number());
        assertEquals("777-555-1212", changedUser.person().contactInformation().secondaryTelephone().number());
        assertEquals("123 Pine Street", changedUser.person().contactInformation().postalAddress().streetAddress());
        assertEquals("Loveland", changedUser.person().contactInformation().postalAddress().city());
    }

    public void testChangeUserEmailAddress() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        ApplicationServiceRegistry
            .identityApplicationService()
            .changeUserEmailAddress(
                    new ChangeEmailAddressCommand(
                            user.tenantId().id(),
                            user.username(),
                            "mynewemailaddress@saasovation.com"));

        User changedUser =
                DomainRegistry
                    .userRepository()
                    .userWithUsername(
                            user.tenantId(),
                            user.username());

        assertNotNull(changedUser);
        assertEquals("mynewemailaddress@saasovation.com", changedUser.person().emailAddress().address());
    }

    public void testChangeUserPostalAddress() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        ApplicationServiceRegistry
            .identityApplicationService()
            .changeUserPostalAddress(
                    new ChangePostalAddressCommand(
                            user.tenantId().id(),
                            user.username(),
                            "123 Pine Street",
                            "Loveland",
                            "CO",
                            "80771",
                            "US"));

        User changedUser =
                DomainRegistry
                    .userRepository()
                    .userWithUsername(
                            user.tenantId(),
                            user.username());

        assertNotNull(changedUser);
        assertEquals("123 Pine Street", changedUser.person().contactInformation().postalAddress().streetAddress());
        assertEquals("Loveland", changedUser.person().contactInformation().postalAddress().city());
    }

    public void testChangeUserPrimaryTelephone() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        ApplicationServiceRegistry
            .identityApplicationService()
            .changeUserPrimaryTelephone(
                    new ChangePrimaryTelephoneCommand(
                            user.tenantId().id(),
                            user.username(),
                            "777-555-1211"));

        User changedUser =
                DomainRegistry
                    .userRepository()
                    .userWithUsername(
                            user.tenantId(),
                            user.username());

        assertNotNull(changedUser);
        assertEquals("777-555-1211", changedUser.person().contactInformation().primaryTelephone().number());
    }

    public void testChangeUserSecondaryTelephone() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        ApplicationServiceRegistry
            .identityApplicationService()
            .changeUserSecondaryTelephone(
                    new ChangeSecondaryTelephoneCommand(
                            user.tenantId().id(),
                            user.username(),
                            "777-555-1212"));

        User changedUser =
                DomainRegistry
                    .userRepository()
                    .userWithUsername(
                            user.tenantId(),
                            user.username());

        assertNotNull(changedUser);
        assertEquals("777-555-1212", changedUser.person().contactInformation().secondaryTelephone().number());
    }

    public void testChangeUserPassword() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        ApplicationServiceRegistry
            .identityApplicationService()
            .changeUserPassword(
                    new ChangeUserPasswordCommand(
                            user.tenantId().id(),
                            user.username(),
                            FIXTURE_PASSWORD,
                            "THIS.IS.JOE'S.NEW.PASSWORD"));

        UserDescriptor userDescriptor =
                ApplicationServiceRegistry
                    .identityApplicationService()
                    .authenticateUser(new AuthenticateUserCommand(
                            user.tenantId().id(),
                            user.username(),
                            "THIS.IS.JOE'S.NEW.PASSWORD"));

        assertNotNull(userDescriptor);
        assertEquals(user.username(), userDescriptor.username());
    }

    public void testChangeUserPersonalName() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        ApplicationServiceRegistry
            .identityApplicationService()
            .changeUserPersonalName(
                    new ChangeUserPersonalNameCommand(
                            user.tenantId().id(),
                            user.username(),
                            "World",
                            "Peace"));

        User changedUser =
                DomainRegistry
                    .userRepository()
                    .userWithUsername(
                            user.tenantId(),
                            user.username());

        assertNotNull(changedUser);
        assertEquals("World Peace", changedUser.person().name().asFormattedName());
    }

    public void testDefineUserEnablement() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        Date now = new Date();
        Date then = new Date(now.getTime() + (60 * 60 * 24 * 365 * 1000));

        ApplicationServiceRegistry
            .identityApplicationService()
            .defineUserEnablement(
                    new DefineUserEnablementCommand(
                            user.tenantId().id(),
                            user.username(),
                            true,
                            now,
                            then));

        User changedUser =
                DomainRegistry
                    .userRepository()
                    .userWithUsername(
                            user.tenantId(),
                            user.username());

        assertNotNull(changedUser);
        assertTrue(changedUser.isEnabled());
    }

    public void testIsGroupMember() throws Exception {
        Group parentGroup = this.group1Aggregate();
        DomainRegistry.groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        DomainRegistry.groupRepository().add(childGroup);

        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        assertEquals(0, parentGroup.groupMembers().size());
        assertEquals(0, childGroup.groupMembers().size());

        parentGroup.addGroup(childGroup, DomainRegistry.groupMemberService());
        childGroup.addUser(user);

        assertTrue(
                ApplicationServiceRegistry
                    .identityApplicationService()
                    .isGroupMember(
                            parentGroup.tenantId().id(),
                            parentGroup.name(),
                            user.username()));

        assertTrue(
                ApplicationServiceRegistry
                    .identityApplicationService()
                    .isGroupMember(
                            childGroup.tenantId().id(),
                            childGroup.name(),
                            user.username()));
    }

    public void testRemoveGroupFromGroup() throws Exception {
        Group parentGroup = this.group1Aggregate();
        DomainRegistry.groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        DomainRegistry.groupRepository().add(childGroup);

        parentGroup.addGroup(childGroup, DomainRegistry.groupMemberService());

        assertEquals(1, parentGroup.groupMembers().size());

        ApplicationServiceRegistry
            .identityApplicationService()
            .removeGroupFromGroup(new RemoveGroupFromGroupCommand(
                    parentGroup.tenantId().id(),
                    parentGroup.name(),
                    childGroup.name()));

        assertEquals(0, parentGroup.groupMembers().size());
    }

    public void testRemoveUserFromGroup() throws Exception {
        Group parentGroup = this.group1Aggregate();
        DomainRegistry.groupRepository().add(parentGroup);

        Group childGroup = this.group2Aggregate();
        DomainRegistry.groupRepository().add(childGroup);

        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        parentGroup.addGroup(childGroup, DomainRegistry.groupMemberService());
        childGroup.addUser(user);

        assertEquals(1, parentGroup.groupMembers().size());
        assertEquals(1, childGroup.groupMembers().size());
        assertTrue(parentGroup.isMember(user, DomainRegistry.groupMemberService()));
        assertTrue(childGroup.isMember(user, DomainRegistry.groupMemberService()));

        ApplicationServiceRegistry
            .identityApplicationService()
            .removeUserFromGroup(new RemoveUserFromGroupCommand(
                childGroup.tenantId().id(),
                childGroup.name(),
                user.username()));

        assertEquals(1, parentGroup.groupMembers().size());
        assertEquals(0, childGroup.groupMembers().size());
        assertFalse(parentGroup.isMember(user, DomainRegistry.groupMemberService()));
        assertFalse(childGroup.isMember(user, DomainRegistry.groupMemberService()));
    }

    public void testQueryTenant() throws Exception {
        Tenant tenant = this.tenantAggregate();

        Tenant queriedTenant =
                ApplicationServiceRegistry
                    .identityApplicationService()
                    .tenant(tenant.tenantId().id());

        assertNotNull(queriedTenant);
        assertEquals(tenant, queriedTenant);
    }

    public void testQueryUser() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        User queriedUser =
                ApplicationServiceRegistry
                    .identityApplicationService()
                    .user(user.tenantId().id(), user.username());

        assertNotNull(user);
        assertEquals(user, queriedUser);
    }

    public void testQueryUserDescriptor() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        UserDescriptor queriedUserDescriptor =
                ApplicationServiceRegistry
                    .identityApplicationService()
                    .userDescriptor(user.tenantId().id(), user.username());

        assertNotNull(user);
        assertEquals(user.userDescriptor(), queriedUserDescriptor);
    }
}
