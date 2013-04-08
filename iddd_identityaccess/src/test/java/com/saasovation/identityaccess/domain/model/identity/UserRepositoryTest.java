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

import java.util.Collection;

import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.IdentityAccessTest;

public class UserRepositoryTest extends IdentityAccessTest {

    public void testAddUser() throws Exception {

        User user = this.userAggregate();

        DomainRegistry.userRepository().add(user);

        assertNotNull(DomainRegistry
                    .userRepository()
                    .userWithUsername(user.tenantId(), user.username()));
    }

    public void testFindUserByUsername() throws Exception {

        User user = this.userAggregate();

        DomainRegistry.userRepository().add(user);

        assertNotNull(DomainRegistry
                .userRepository()
                .userWithUsername(user.tenantId(), user.username()));
    }

    public void testRemoveUser() throws Exception {

        User user = this.userAggregate();

        DomainRegistry.userRepository().add(user);

        assertNotNull(DomainRegistry
                    .userRepository()
                    .userWithUsername(user.tenantId(), user.username()));

        DomainRegistry.userRepository().remove(user);

        assertNull(DomainRegistry
                    .userRepository()
                    .userWithUsername(user.tenantId(), user.username()));
    }

    public void testFindSimilarlyNamedUsers() throws Exception {

        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        User user2 = this.userAggregate2();
        DomainRegistry.userRepository().add(user2);

        FullName name = user.person().name();

        Collection<User> users =
            DomainRegistry
                .userRepository()
                .allSimilarlyNamedUsers(
                        user.tenantId(),
                        "",
                        name.lastName().substring(0, 2));

        assertEquals(users.size(), 2);
    }

}
