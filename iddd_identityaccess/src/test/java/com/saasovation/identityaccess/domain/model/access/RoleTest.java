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

package com.saasovation.identityaccess.domain.model.access;

import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.domain.model.DomainEventSubscriber;
import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.IdentityAccessTest;
import com.saasovation.identityaccess.domain.model.identity.Group;
import com.saasovation.identityaccess.domain.model.identity.GroupGroupAdded;
import com.saasovation.identityaccess.domain.model.identity.GroupGroupRemoved;
import com.saasovation.identityaccess.domain.model.identity.GroupUserAdded;
import com.saasovation.identityaccess.domain.model.identity.GroupUserRemoved;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.User;

public class RoleTest extends IdentityAccessTest {

    private int groupSomethingAddedCount;
    private int groupSomethingRemovedCount;
    private int roleSomethingAssignedCount;
    private int roleSomethingUnassignedCount;

    public RoleTest() {
        super();
    }

    public void testProvisionRole() throws Exception {
        Tenant tenant = this.tenantAggregate();
        Role role = tenant.provisionRole("Manager", "A manager role.");
        DomainRegistry.roleRepository().add(role);
        assertEquals(1, DomainRegistry.roleRepository().allRoles(tenant.tenantId()).size());
    }

    public void testRoleUniqueness() throws Exception {
        Tenant tenant = this.tenantAggregate();
        Role role1 = tenant.provisionRole("Manager", "A manager role.");
        DomainRegistry.roleRepository().add(role1);

        boolean nonUnique = false;

        try {
            Role role2 = tenant.provisionRole("Manager", "A manager role.");
            DomainRegistry.roleRepository().add(role2);

            fail("Should have thrown exception for nonuniqueness.");

        } catch (IllegalStateException e) {
            nonUnique = true;
        }

        assertTrue(nonUnique);
    }

    public void testUserIsInRole() throws Exception {
        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = new Group(user.tenantId(), "Managers", "A group of managers.");
        DomainRegistry.groupRepository().add(group);
        managerRole.assignGroup(group, DomainRegistry.groupMemberService());
        DomainRegistry.roleRepository().add(managerRole);
        group.addUser(user);

        assertTrue(group.isMember(user, DomainRegistry.groupMemberService()));
        assertTrue(managerRole.isInRole(user, DomainRegistry.groupMemberService()));
    }

    public void testUserIsNotInRole() throws Exception {
        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = tenant.provisionGroup("Managers", "A group of managers.");
        DomainRegistry.groupRepository().add(group);
        managerRole.assignGroup(group, DomainRegistry.groupMemberService());
        DomainRegistry.roleRepository().add(managerRole);
        Role accountantRole = new Role(user.tenantId(), "Accountant", "An accountant role.");
        DomainRegistry.roleRepository().add(accountantRole);

        assertFalse(managerRole.isInRole(user, DomainRegistry.groupMemberService()));
        assertFalse(accountantRole.isInRole(user, DomainRegistry.groupMemberService()));
    }

    public void testNoRoleInternalGroupsInFindGroupByName() throws Exception {
        Tenant tenant = this.tenantAggregate();
        Role roleA = tenant.provisionRole("RoleA", "A role of A.");
        DomainRegistry.roleRepository().add(roleA);

        boolean error = false;

        try {

            System.out.println("GROUP REPOSITORY: " + DomainRegistry.groupRepository());

            DomainRegistry
                .groupRepository()
                .groupNamed(
                        tenant.tenantId(),
                        roleA.group().name());

            fail("Should have thrown exception for invalid group name.");

        } catch (Exception e) {
            error = true;
        }

        assertTrue(error);
    }

    public void testInternalGroupAddedEventsNotPublished() throws Exception {
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<GroupAssignedToRole>() {
            @Override
            public void handleEvent(GroupAssignedToRole aDomainEvent) {
                ++roleSomethingAssignedCount;
            }

            @Override
            public Class<GroupAssignedToRole> subscribedToEventType() {
                return GroupAssignedToRole.class;
            }
        });
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<GroupGroupAdded>() {
            @Override
            public void handleEvent(GroupGroupAdded aDomainEvent) {
                ++groupSomethingAddedCount;
            }

            @Override
            public Class<GroupGroupAdded> subscribedToEventType() {
                return GroupGroupAdded.class;
            }
        });
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<UserAssignedToRole>() {
            @Override
            public void handleEvent(UserAssignedToRole aDomainEvent) {
                ++roleSomethingAssignedCount;
            }

            @Override
            public Class<UserAssignedToRole> subscribedToEventType() {
                return UserAssignedToRole.class;
            }
        });
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<GroupUserAdded>() {
            @Override
            public void handleEvent(GroupUserAdded aDomainEvent) {
                ++groupSomethingAddedCount;
            }

            @Override
            public Class<GroupUserAdded> subscribedToEventType() {
                return GroupUserAdded.class;
            }
        });

        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = new Group(user.tenantId(), "Managers", "A group of managers.");
        DomainRegistry.groupRepository().add(group);
        managerRole.assignGroup(group, DomainRegistry.groupMemberService());
        managerRole.assignUser(user);
        DomainRegistry.roleRepository().add(managerRole);
        group.addUser(user); // legal add

        assertEquals(2, roleSomethingAssignedCount);
        assertEquals(1, groupSomethingAddedCount);
    }

    public void testInternalGroupRemovedEventsNotPublished() throws Exception {
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<GroupUnassignedFromRole>() {
            @Override
            public void handleEvent(GroupUnassignedFromRole aDomainEvent) {
                ++roleSomethingUnassignedCount;
            }

            @Override
            public Class<GroupUnassignedFromRole> subscribedToEventType() {
                return GroupUnassignedFromRole.class;
            }
        });
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<GroupGroupRemoved>() {
            @Override
            public void handleEvent(GroupGroupRemoved aDomainEvent) {
                ++groupSomethingRemovedCount;
            }

            @Override
            public Class<GroupGroupRemoved> subscribedToEventType() {
                return GroupGroupRemoved.class;
            }
        });
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<UserUnassignedFromRole>() {
            @Override
            public void handleEvent(UserUnassignedFromRole aDomainEvent) {
                ++roleSomethingUnassignedCount;
            }

            @Override
            public Class<UserUnassignedFromRole> subscribedToEventType() {
                return UserUnassignedFromRole.class;
            }
        });
        DomainEventPublisher.instance().subscribe(new DomainEventSubscriber<GroupUserRemoved>() {
            @Override
            public void handleEvent(GroupUserRemoved aDomainEvent) {
                ++groupSomethingRemovedCount;
            }

            @Override
            public Class<GroupUserRemoved> subscribedToEventType() {
                return GroupUserRemoved.class;
            }
        });

        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);
        Group group = new Group(user.tenantId(), "Managers", "A group of managers.");
        DomainRegistry.groupRepository().add(group);
        managerRole.assignUser(user);
        managerRole.assignGroup(group, DomainRegistry.groupMemberService());
        DomainRegistry.roleRepository().add(managerRole);

        managerRole.unassignUser(user);
        managerRole.unassignGroup(group);

        assertEquals(2, roleSomethingUnassignedCount);
        assertEquals(0, groupSomethingRemovedCount);
    }
}
