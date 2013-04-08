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

import com.saasovation.identityaccess.domain.model.IdentityAccessTest;

public class FullNameTest extends IdentityAccessTest {

    private final static String FIRST_NAME = "Zoe";
    private final static String LAST_NAME = "Doe";
    private final static String MARRIED_LAST_NAME = "Jones-Doe";
    private final static String WRONG_FIRST_NAME = "Zeo";

    public FullNameTest() {
        super();
    }

    public void testChangedFirstName() throws Exception {
        FullName name = new FullName(WRONG_FIRST_NAME, LAST_NAME);
        name = name.withChangedFirstName(FIRST_NAME);
        assertEquals(FIRST_NAME + " " + LAST_NAME, name.asFormattedName());
    }

    public void testChangedLastName() throws Exception {
        FullName name = new FullName(FIRST_NAME, LAST_NAME);
        name = name.withChangedLastName(MARRIED_LAST_NAME);
        assertEquals(FIRST_NAME + " " + MARRIED_LAST_NAME, name.asFormattedName());
    }

    public void testFormattedName() throws Exception {
        FullName name = new FullName(FIRST_NAME, LAST_NAME);
        assertEquals(FIRST_NAME + " " + LAST_NAME, name.asFormattedName());
    }
}
