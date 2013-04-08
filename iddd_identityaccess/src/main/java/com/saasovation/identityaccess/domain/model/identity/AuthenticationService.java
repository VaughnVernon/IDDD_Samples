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

import com.saasovation.common.AssertionConcern;

public class AuthenticationService extends AssertionConcern {

    private EncryptionService encryptionService;
    private TenantRepository tenantRepository;
    private UserRepository userRepository;

    public AuthenticationService(
            TenantRepository aTenantRepository,
            UserRepository aUserRepository,
            EncryptionService anEncryptionService) {

        super();

        this.encryptionService = anEncryptionService;
        this.tenantRepository = aTenantRepository;
        this.userRepository = aUserRepository;
    }

    public UserDescriptor authenticate(
            TenantId aTenantId,
            String aUsername,
            String aPassword) {

        this.assertArgumentNotNull(aTenantId, "TenantId must not be null.");
        this.assertArgumentNotEmpty(aUsername, "Username must be provided.");
        this.assertArgumentNotEmpty(aPassword, "Password must be provided.");

        UserDescriptor userDescriptor = UserDescriptor.nullDescriptorInstance();

        Tenant tenant = this.tenantRepository().tenantOfId(aTenantId);

        if (tenant != null && tenant.isActive()) {
            String encryptedPassword = this.encryptionService().encryptedValue(aPassword);

            User user =
                    this.userRepository()
                        .userFromAuthenticCredentials(
                            aTenantId,
                            aUsername,
                            encryptedPassword);

            if (user != null && user.isEnabled()) {
                userDescriptor = user.userDescriptor();
            }
        }

        return userDescriptor;
    }

    private EncryptionService encryptionService() {
        return this.encryptionService;
    }

    private TenantRepository tenantRepository() {
        return this.tenantRepository;
    }

    private UserRepository userRepository() {
        return this.userRepository;
    }
}
