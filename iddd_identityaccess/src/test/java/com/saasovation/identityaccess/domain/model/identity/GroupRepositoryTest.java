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

import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.IdentityAccessTest;

public class GroupRepositoryTest extends IdentityAccessTest {

    public GroupRepositoryTest() {
        super();
    }

    public void testRemoveGroupReferencedUser() throws Exception {
        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);
        groupA.addUser(user);
        DomainRegistry.groupRepository().add(groupA);

        assertEquals(groupA.groupMembers().size(), 1);
        assertTrue(groupA.isMember(user, DomainRegistry.groupMemberService()));
        DomainRegistry.userRepository().remove(user);
        this.session().flush();
        this.session().evict(groupA);
        Group reGrouped =
            DomainRegistry
                .groupRepository()
                .groupNamed(tenant.tenantId(), "GroupA");
        assertEquals("GroupA", reGrouped.name());
        assertEquals(1, reGrouped.groupMembers().size());
        assertFalse(reGrouped.isMember(user, DomainRegistry.groupMemberService()));
    }

    public void testRepositoryRemoveGroup() throws Exception {
        Tenant tenant = this.tenantAggregate();
        Group groupA = tenant.provisionGroup("GroupA", "A group named GroupA");
        DomainRegistry.groupRepository().add(groupA);
        Group notNullGroup =
            DomainRegistry
                .groupRepository()
                .groupNamed(tenant.tenantId(), "GroupA");
        assertNotNull(notNullGroup);
        DomainRegistry.groupRepository().remove(groupA);
        Group nullGroup =
            DomainRegistry
                .groupRepository()
                .groupNamed(tenant.tenantId(), "GroupA");
        assertNull(nullGroup);
    }
}
