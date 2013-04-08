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

import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.IdentityAccessTest;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.User;

public class AuthorizationServiceTest extends IdentityAccessTest {

    public AuthorizationServiceTest() {
        super();
    }

    public void testUserInRoleAuthorization() throws Exception {

        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);

        managerRole.assignUser(user);

        DomainRegistry
            .roleRepository()
            .add(managerRole);

        boolean authorized =
                DomainRegistry
                    .authorizationService()
                    .isUserInRole(user, "Manager");

        assertTrue(authorized);

        authorized =
                DomainRegistry
                    .authorizationService()
                    .isUserInRole(user, "Director");

        assertFalse(authorized);
    }

    public void testUsernameInRoleAuthorization() throws Exception {

        Tenant tenant = this.tenantAggregate();
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);
        Role managerRole = tenant.provisionRole("Manager", "A manager role.", true);

        managerRole.assignUser(user);

        DomainRegistry
            .roleRepository()
            .add(managerRole);

        boolean authorized =
                DomainRegistry
                    .authorizationService()
                    .isUserInRole(tenant.tenantId(), user.username(), "Manager");

        assertTrue(authorized);

        authorized =
                DomainRegistry
                    .authorizationService()
                    .isUserInRole(tenant.tenantId(), user.username(), "Director");

        assertFalse(authorized);
    }
}
