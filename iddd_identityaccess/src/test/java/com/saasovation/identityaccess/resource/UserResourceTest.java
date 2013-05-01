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

package com.saasovation.identityaccess.resource;

import java.util.UUID;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.saasovation.common.media.RepresentationReader;
import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.access.Role;
import com.saasovation.identityaccess.domain.model.identity.User;

public class UserResourceTest extends ResourceTestCase {

    public UserResourceTest() {
        super();
    }

    public void testGetAuthenticUser() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        String url = "http://localhost:" + PORT + "/tenants/{tenantId}/users/{username}/autenticatedWith/{password}";

        System.out.println(">>> GET: " + url);
        ClientRequest request = new ClientRequest(url);
        request.pathParameter("tenantId", user.tenantId().id());
        request.pathParameter("username", user.username());
        request.pathParameter("password", FIXTURE_PASSWORD);

        String output = request.getTarget(String.class);
        System.out.println(output);

        RepresentationReader reader = new RepresentationReader(output);

        assertEquals(user.tenantId().id(), reader.stringValue("tenantId.id"));
        assertEquals(user.username(), reader.stringValue("username"));
        assertEquals(user.person().emailAddress().address(), reader.stringValue("emailAddress"));
    }

    public void testGetAuthenticUserWrongPassword() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        String url = "http://localhost:" + PORT + "/tenants/{tenantId}/users/{username}/autenticatedWith/{password}";

        System.out.println(">>> GET: " + url);
        ClientRequest request = new ClientRequest(url);
        request.pathParameter("tenantId", user.tenantId().id());
        request.pathParameter("username", user.username());
        request.pathParameter("password", UUID.randomUUID().toString());
        ClientResponse<String> response = request.get(String.class);
        assertTrue(response.getStatus() == 404 || response.getStatus() == 500);
    }

    public void testGetUser() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        String url = "http://localhost:" + PORT + "/tenants/{tenantId}/users/{username}";

        System.out.println(">>> GET: " + url);
        ClientRequest request = new ClientRequest(url);
        request.pathParameter("tenantId", user.tenantId().id());
        request.pathParameter("username", user.username());
        ClientResponse<String> response = request.get(String.class);
        assertEquals(200, response.getStatus());
        String entity = response.getEntity();
        System.out.println(entity);
        RepresentationReader reader = new RepresentationReader(entity);
        assertEquals(user.username(), reader.stringValue("username"));
        assertTrue(reader.booleanValue("enabled"));
    }

    public void testGetNonExistingUser() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        String url = "http://localhost:" + PORT + "/tenants/{tenantId}/users/{username}";

        System.out.println(">>> GET: " + url);
        ClientRequest request = new ClientRequest(url);
        request.pathParameter("tenantId", user.tenantId().id());
        request.pathParameter("username", user.username() + "!");
        ClientResponse<String> response = request.get(String.class);
        assertTrue(response.getStatus() == 404 || response.getStatus() == 500);
    }

    public void testIsUserInRole() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        Role role = this.roleAggregate();
        role.assignUser(user);
        DomainRegistry.roleRepository().add(role);

        String url = "http://localhost:" + PORT + "/tenants/{tenantId}/users/{username}/inRole/{role}";

        System.out.println(">>> GET: " + url);
        ClientRequest request = new ClientRequest(url);
        request.pathParameter("tenantId", user.tenantId().id());
        request.pathParameter("username", user.username());
        request.pathParameter("role", role.name());
        ClientResponse<String> response = request.get(String.class);
        assertEquals(200, response.getStatus());
        String entity = response.getEntity();
        System.out.println(entity);
        RepresentationReader reader = new RepresentationReader(entity);
        assertEquals(user.username(),  reader.stringValue("username"));
        assertEquals(role.name(), reader.stringValue("role"));
    }

    public void testIsUserNotInRole() throws Exception {
        User user = this.userAggregate();
        DomainRegistry.userRepository().add(user);

        Role role = this.roleAggregate();
        DomainRegistry.roleRepository().add(role);

        String url = "http://localhost:" + PORT + "/tenants/{tenantId}/users/{username}/inRole/{role}";

        System.out.println(">>> GET: " + url);
        ClientRequest request = new ClientRequest(url);
        request.pathParameter("tenantId", user.tenantId().id());
        request.pathParameter("username", user.username());
        request.pathParameter("role", role.name());
        ClientResponse<String> response = request.get(String.class);
        assertEquals(204, response.getStatus());
    }
}
