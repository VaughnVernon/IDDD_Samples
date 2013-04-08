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

package com.saasovation.collaboration.domain.model.collaborator;

import com.saasovation.collaboration.domain.model.DomainTest;
import com.saasovation.collaboration.domain.model.tenant.Tenant;
import com.saasovation.collaboration.port.adapter.service.CollaboratorTranslator;
import com.saasovation.collaboration.port.adapter.service.TranslatingCollaboratorService;
import com.saasovation.collaboration.port.adapter.service.UserInRoleAdapter;

public class CollaboratorServiceTest extends DomainTest {

    private static final String USER_IN_ROLE_REPRESENTATION =
            "{"
            + "\"role\":\"UNUSED\",\"username\":\"zoe\","
            + "\"tenantId\":\"A94A8298-43B8-4DA0-9917-13FFF9E116ED\","
            + "\"firstName\":\"Zoe\",\"lastName\":\"Doe\","
            + "\"emailAddress\":\"zoe@saasovation.com\""
            + "}";

    private CollaboratorService collaboratorService;

    public CollaboratorServiceTest() {
        super();
    }

    public void testAuthorFrom() throws Exception {
        Author author =
                this.collaboratorService.authorFrom(
                        new Tenant("12345"),
                        "zdoe");

        assertNotNull(author);
    }

    public void testCreatorFrom() throws Exception {
        Creator creator =
                this.collaboratorService.creatorFrom(
                        new Tenant("12345"),
                        "zdoe");

        assertNotNull(creator);
    }

    public void testModeratorFrom() throws Exception {
        Moderator moderator =
                this.collaboratorService.moderatorFrom(
                        new Tenant("12345"),
                        "zdoe");

        assertNotNull(moderator);
    }

    public void testOwnerFrom() throws Exception {
        Owner owner =
                this.collaboratorService.ownerFrom(
                        new Tenant("12345"),
                        "zdoe");

        assertNotNull(owner);
    }

    public void testParticipantFrom() throws Exception {
        Participant participant =
                this.collaboratorService.participantFrom(
                        new Tenant("12345"),
                        "zdoe");

        assertNotNull(participant);
    }

    public void testCollaboratorTranslator() throws Exception {
        Author collaborator =
                new CollaboratorTranslator()
                    .toCollaboratorFromRepresentation(
                        USER_IN_ROLE_REPRESENTATION,
                        Author.class);

        assertNotNull(collaborator);
        assertEquals("zoe", collaborator.identity());
        assertEquals("zoe@saasovation.com", collaborator.emailAddress());
        assertEquals("Zoe Doe", collaborator.name());
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.collaboratorService =
                new TranslatingCollaboratorService(
                        new MockUserInRoleAdapter());
    }

    private static class MockUserInRoleAdapter implements UserInRoleAdapter {

        public MockUserInRoleAdapter() {
            super();
        }

        @Override
        public <T extends Collaborator> T toCollaborator(
                Tenant aTenant,
                String anIdentity,
                String aRoleName,
                Class<T> aCollaboratorClass)
        {
            T collaborator = null;

            // only eliminates the HTTP client;
            // still uses translator

            try {
                collaborator =
                        new CollaboratorTranslator()
                            .toCollaboratorFromRepresentation(
                                USER_IN_ROLE_REPRESENTATION,
                                aCollaboratorClass);

            } catch (Exception e) {
                throw new RuntimeException("Cannot adapt " + aRoleName, e);
            }

            return collaborator;
        }
    }
}
