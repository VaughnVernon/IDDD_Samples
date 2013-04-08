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

public class CollaboratorTest extends DomainTest {

    public void testAuthorEquals() throws Exception {
        Author author1 = new Author("jdoe", "John Doe", "jdoe@saasovation.com");
        Author author2 = new Author("jdoe", "John Doe", "jdoe@saasovation.com");
        Author author3 = new Author("zdoe", "Zoe Doe", "zdoe@saasovation.com");

        assertEquals(author1, author2);
        assertNotSame(author1, author2);
        assertFalse(author1.equals(author3));
        assertFalse(author2.equals(author3));
    }

    public void testCreatorEquals() throws Exception {
        Creator creator1 = new Creator("jdoe", "John Doe", "jdoe@saasovation.com");
        Creator creator2 = new Creator("jdoe", "John Doe", "jdoe@saasovation.com");
        Creator creator3 = new Creator("zdoe", "Zoe Doe", "zdoe@saasovation.com");

        assertEquals(creator1, creator2);
        assertNotSame(creator1, creator2);
        assertFalse(creator1.equals(creator3));
        assertFalse(creator2.equals(creator3));
    }

    public void testOwnerEquals() throws Exception {
        Owner owner1 = new Owner("jdoe", "John Doe", "jdoe@saasovation.com");
        Owner owner2 = new Owner("jdoe", "John Doe", "jdoe@saasovation.com");
        Owner owner3 = new Owner("zdoe", "Zoe Doe", "zdoe@saasovation.com");

        assertEquals(owner1, owner2);
        assertNotSame(owner1, owner2);
        assertFalse(owner1.equals(owner3));
        assertFalse(owner2.equals(owner3));
    }

    public void testParticipantEquals() throws Exception {
        Participant participant1 = new Participant("jdoe", "John Doe", "jdoe@saasovation.com");
        Participant participant2 = new Participant("jdoe", "John Doe", "jdoe@saasovation.com");
        Participant participant3 = new Participant("zdoe", "Zoe Doe", "zdoe@saasovation.com");

        assertEquals(participant1, participant2);
        assertNotSame(participant1, participant2);
        assertFalse(participant1.equals(participant3));
        assertFalse(participant2.equals(participant3));
    }

    public void testRoleIdentityLimits() throws Exception {

        boolean failed = false;

        try {
            new Author("", "", "");

            fail("Should have thrown exception.");

        } catch (Throwable t) {
            failed = true;
        }

        assertTrue(failed);

        failed = false;

        try {
            new Author(
                    "01234567890123456789012345678901234567890123456789" + "x",
                    "Some Name",
                    "doh@saasovation.com");

            fail("Should have thrown exception.");

        } catch (Throwable t) {
            failed = true;
        }

        assertTrue(failed);
    }
}
