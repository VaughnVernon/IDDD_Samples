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

import com.saasovation.identityaccess.application.command.AssignUserToRoleCommand;
import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.access.Role;
import com.saasovation.identityaccess.domain.model.identity.User;

public class AccessApplicationServiceTest extends ApplicationServiceTest {

    public AccessApplicationServiceTest() {
        super();
    }

    public void testAssignUserToRole() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        Role role = this.roleAggregate();
        DomainRegistry.roleRepository().add(role);

        assertFalse(role.isInRole(user, DomainRegistry.groupMemberService()));

        ApplicationServiceRegistry
            .accessApplicationService()
            .assignUserToRole(
                    new AssignUserToRoleCommand(
                            user.tenantId().id(),
                            user.username(),
                            role.name()));

        assertTrue(role.isInRole(user, DomainRegistry.groupMemberService()));
    }

    public void testIsUserInRole() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        Role role = this.roleAggregate();
        DomainRegistry.roleRepository().add(role);

        assertFalse(
                ApplicationServiceRegistry
                    .accessApplicationService()
                    .isUserInRole(
                            user.tenantId().id(),
                            user.username(),
                            role.name()));

        ApplicationServiceRegistry
            .accessApplicationService()
            .assignUserToRole(
                    new AssignUserToRoleCommand(
                            user.tenantId().id(),
                            user.username(),
                            role.name()));

        assertTrue(
                ApplicationServiceRegistry
                    .accessApplicationService()
                    .isUserInRole(
                            user.tenantId().id(),
                            user.username(),
                            role.name()));
    }

    public void testUserInRole() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        Role role = this.roleAggregate();
        DomainRegistry.roleRepository().add(role);


        User userNotInRole =
                ApplicationServiceRegistry
                    .accessApplicationService()
                    .userInRole(user.tenantId().id(), user.username(), role.name());

        assertNull(userNotInRole);

        ApplicationServiceRegistry
            .accessApplicationService()
            .assignUserToRole(
                    new AssignUserToRoleCommand(
                            user.tenantId().id(),
                            user.username(),
                            role.name()));

        User userInRole =
                ApplicationServiceRegistry
                    .accessApplicationService()
                    .userInRole(user.tenantId().id(), user.username(), role.name());

        assertNotNull(userInRole);
    }
}
