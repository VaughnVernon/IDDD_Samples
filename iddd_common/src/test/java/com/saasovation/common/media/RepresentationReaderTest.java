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

package com.saasovation.common.media;

import junit.framework.TestCase;

public class RepresentationReaderTest extends TestCase {

    private static final String USER_IN_ROLE_REPRESENTATION =
            "{"
            + "\"role\":\"Author\",\"username\":\"zoe\","
            + "\"tenantId\":\"A94A8298-43B8-4DA0-9917-13FFF9E116ED\","
            + "\"firstName\":\"Zoe\",\"lastName\":\"Doe\","
            + "\"emailAddress\":\"zoe@saasovation.com\""
            + "}";

    public RepresentationReaderTest() {
        super();
    }

    public void testUserInRoleRepresentation() throws Exception {
        RepresentationReader reader =
                new RepresentationReader(USER_IN_ROLE_REPRESENTATION);

        assertEquals("Author", reader.stringValue("role"));
        assertEquals("zoe", reader.stringValue("username"));
        assertEquals("A94A8298-43B8-4DA0-9917-13FFF9E116ED", reader.stringValue("tenantId"));
        assertEquals("Zoe", reader.stringValue("firstName"));
        assertEquals("Doe", reader.stringValue("lastName"));
        assertEquals("zoe@saasovation.com", reader.stringValue("emailAddress"));
    }
}
