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

public class PasswordServiceTest extends IdentityAccessTest {

    public PasswordServiceTest() {
        super();
    }

    public void testGenerateStrongPassword() throws Exception {
        String password =
                DomainRegistry
                    .passwordService()
                    .generateStrongPassword();

        assertTrue(DomainRegistry.passwordService().isStrong(password));
        assertFalse(DomainRegistry.passwordService().isWeak(password));
    }

    public void testIsStrongPassword() throws Exception {
        final String password = "Th1sShudBStrong.";
        assertTrue(DomainRegistry.passwordService().isStrong(password));
        assertFalse(DomainRegistry.passwordService().isVeryStrong(password));
        assertFalse(DomainRegistry.passwordService().isWeak(password));
    }

    public void testIsVeryStrongPassword() throws Exception {
        final String password = "Th1sSh0uldBV3ryStrong!";
        assertTrue(DomainRegistry.passwordService().isVeryStrong(password));
        assertTrue(DomainRegistry.passwordService().isStrong(password));
        assertFalse(DomainRegistry.passwordService().isWeak(password));
    }

    public void testIsWeakPassword() throws Exception {
        final String password = "Weakness";
        assertFalse(DomainRegistry.passwordService().isVeryStrong(password));
        assertFalse(DomainRegistry.passwordService().isStrong(password));
        assertTrue(DomainRegistry.passwordService().isWeak(password));
    }
}
