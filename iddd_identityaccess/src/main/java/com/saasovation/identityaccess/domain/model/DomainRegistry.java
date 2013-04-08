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

package com.saasovation.identityaccess.domain.model;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.saasovation.identityaccess.domain.model.access.AuthorizationService;
import com.saasovation.identityaccess.domain.model.access.RoleRepository;
import com.saasovation.identityaccess.domain.model.identity.AuthenticationService;
import com.saasovation.identityaccess.domain.model.identity.EncryptionService;
import com.saasovation.identityaccess.domain.model.identity.GroupMemberService;
import com.saasovation.identityaccess.domain.model.identity.GroupRepository;
import com.saasovation.identityaccess.domain.model.identity.PasswordService;
import com.saasovation.identityaccess.domain.model.identity.TenantProvisioningService;
import com.saasovation.identityaccess.domain.model.identity.TenantRepository;
import com.saasovation.identityaccess.domain.model.identity.UserRepository;

public class DomainRegistry implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static AuthenticationService authenticationService() {
        return (AuthenticationService) applicationContext.getBean("authenticationService");
    }

    public static AuthorizationService authorizationService() {
        return (AuthorizationService) applicationContext.getBean("authorizationService");
    }

    public static EncryptionService encryptionService() {
        return (EncryptionService) applicationContext.getBean("encryptionService");
    }

    public static GroupMemberService groupMemberService() {
        return (GroupMemberService) applicationContext.getBean("groupMemberService");
    }

    public static GroupRepository groupRepository() {
        return (GroupRepository) applicationContext.getBean("groupRepository");
    }

    public static PasswordService passwordService() {
        return (PasswordService) applicationContext.getBean("passwordService");
    }

    public static RoleRepository roleRepository() {
        return (RoleRepository) applicationContext.getBean("roleRepository");
    }

    public static TenantProvisioningService tenantProvisioningService() {
        return (TenantProvisioningService) applicationContext.getBean("tenantProvisioningService");
    }

    public static TenantRepository tenantRepository() {
        return (TenantRepository) applicationContext.getBean("tenantRepository");
    }

    public static UserRepository userRepository() {
        return (UserRepository) applicationContext.getBean("userRepository");
    }

    @Override
    public synchronized void setApplicationContext(
            ApplicationContext anApplicationContext)
    throws BeansException {

        if (DomainRegistry.applicationContext == null) {
            DomainRegistry.applicationContext = anApplicationContext;
        }
    }
}
