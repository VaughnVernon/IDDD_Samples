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

package com.saasovation.collaboration.port.adapter.service;

import com.saasovation.collaboration.domain.model.collaborator.Collaborator;
import com.saasovation.collaboration.domain.model.tenant.Tenant;

public class MockUserInRoleAdapter implements UserInRoleAdapter {

    private static final String USER_IN_ROLE_REPRESENTATION =
            "{"
            + "\"role\":\"UNUSED\",\"username\":\"$uid$\","
            + "\"tenantId\":\"$tid$\","
            + "\"firstName\":\"First\",\"lastName\":\"Last\","
            + "\"emailAddress\":\"$uid$@saasovation.com\""
            + "}";

    public MockUserInRoleAdapter() {
        super();

        System.out.println("LOADED UserInRoleAdapter (MOCK)");
    }

    @Override
    public <T extends Collaborator> T toCollaborator(
            Tenant aTenant,
            String anIdentity,
            String aRoleName,
            Class<T> aCollaboratorClass) {

        String representation = USER_IN_ROLE_REPRESENTATION.replace("$uid$", anIdentity);
        representation = representation.replace("$tid$", aTenant.id());

        T collaborator = null;

        try {
            collaborator =
                    new CollaboratorTranslator()
                        .toCollaboratorFromRepresentation(
                                representation,
                                aCollaboratorClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create collaborator.");
        }

        return collaborator;
    }
}
