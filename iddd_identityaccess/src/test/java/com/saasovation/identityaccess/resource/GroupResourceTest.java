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

import org.jboss.resteasy.client.ClientRequest;

import com.saasovation.common.media.RepresentationReader;
import com.saasovation.identityaccess.domain.model.DomainRegistry;
import com.saasovation.identityaccess.domain.model.identity.Group;

public class GroupResourceTest extends ResourceTestCase {

    public GroupResourceTest() {
        super();
    }

    public void testGetGroup() throws Exception {
        Group group = this.group1Aggregate();
        DomainRegistry.groupRepository().add(group);

        String url = "http://localhost:" + PORT + "/tenants/{tenantId}/groups/{groupName}";

        System.out.println(">>> GET: " + url);
        ClientRequest request = new ClientRequest(url);
        request.pathParameter("tenantId", group.tenantId().id());
        request.pathParameter("groupName", group.name());
        String output = request.getTarget(String.class);
        System.out.println(output);

        RepresentationReader reader = new RepresentationReader(output);

        assertEquals(group.tenantId().id(), reader.stringValue("tenantId.id"));
        assertEquals(group.name(), reader.stringValue("name"));
    }
}
