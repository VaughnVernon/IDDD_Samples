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

package com.saasovation.identityaccess.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.saasovation.identityaccess.application.command.ActivateTenantCommand;
import com.saasovation.identityaccess.application.command.AddGroupToGroupCommand;
import com.saasovation.identityaccess.application.command.AddUserToGroupCommand;
import com.saasovation.identityaccess.application.command.AuthenticateUserCommand;
import com.saasovation.identityaccess.application.command.ChangeContactInfoCommand;
import com.saasovation.identityaccess.application.command.ChangeEmailAddressCommand;
import com.saasovation.identityaccess.application.command.ChangePostalAddressCommand;
import com.saasovation.identityaccess.application.command.ChangePrimaryTelephoneCommand;
import com.saasovation.identityaccess.application.command.ChangeSecondaryTelephoneCommand;
import com.saasovation.identityaccess.application.command.ChangeUserPasswordCommand;
import com.saasovation.identityaccess.application.command.ChangeUserPersonalNameCommand;
import com.saasovation.identityaccess.application.command.DeactivateTenantCommand;
import com.saasovation.identityaccess.application.command.DefineUserEnablementCommand;
import com.saasovation.identityaccess.application.command.ProvisionGroupCommand;
import com.saasovation.identityaccess.application.command.ProvisionTenantCommand;
import com.saasovation.identityaccess.application.command.RegisterUserCommand;
import com.saasovation.identityaccess.application.command.RemoveGroupFromGroupCommand;
import com.saasovation.identityaccess.application.command.RemoveUserFromGroupCommand;
import com.saasovation.identityaccess.domain.model.identity.AuthenticationService;
import com.saasovation.identityaccess.domain.model.identity.ContactInformation;
import com.saasovation.identityaccess.domain.model.identity.EmailAddress;
import com.saasovation.identityaccess.domain.model.identity.Enablement;
import com.saasovation.identityaccess.domain.model.identity.FullName;
import com.saasovation.identityaccess.domain.model.identity.Group;
import com.saasovation.identityaccess.domain.model.identity.GroupMemberService;
import com.saasovation.identityaccess.domain.model.identity.GroupRepository;
import com.saasovation.identityaccess.domain.model.identity.Person;
import com.saasovation.identityaccess.domain.model.identity.PostalAddress;
import com.saasovation.identityaccess.domain.model.identity.Telephone;
import com.saasovation.identityaccess.domain.model.identity.Tenant;
import com.saasovation.identityaccess.domain.model.identity.TenantId;
import com.saasovation.identityaccess.domain.model.identity.TenantProvisioningService;
import com.saasovation.identityaccess.domain.model.identity.TenantRepository;
import com.saasovation.identityaccess.domain.model.identity.User;
import com.saasovation.identityaccess.domain.model.identity.UserDescriptor;
import com.saasovation.identityaccess.domain.model.identity.UserRepository;

@Transactional
public class IdentityApplicationService {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private TenantProvisioningService tenantProvisioningService;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private UserRepository userRepository;

    public IdentityApplicationService() {
        super();

        // IdentityAccessEventProcessor.register();
    }

    @Transactional
    public void activateTenant(ActivateTenantCommand aCommand) {
        Tenant tenant = this.existingTenant(aCommand.getTenantId());

        tenant.activate();
    }

    @Transactional
    public void addGroupToGroup(AddGroupToGroupCommand aCommand) {
        Group parentGroup =
                this.existingGroup(
                        aCommand.getTenantId(),
                        aCommand.getParentGroupName());

        Group childGroup =
                this.existingGroup(
                        aCommand.getTenantId(),
                        aCommand.getChildGroupName());

        parentGroup.addGroup(childGroup, this.groupMemberService());
    }

    @Transactional
    public void addUserToGroup(AddUserToGroupCommand aCommand) {
        Group group =
                this.existingGroup(
                        aCommand.getTenantId(),
                        aCommand.getGroupName());

        User user =
                this.existingUser(
                        aCommand.getTenantId(),
                        aCommand.getUsername());

        group.addUser(user);
    }

    @Transactional(readOnly=true)
    public UserDescriptor authenticateUser(AuthenticateUserCommand aCommand) {
        UserDescriptor userDescriptor =
                this.authenticationService()
                    .authenticate(
                        new TenantId(aCommand.getTenantId()),
                        aCommand.getUsername(),
                        aCommand.getPassword());

        return userDescriptor;
    }

    @Transactional
    public void deactivateTenant(DeactivateTenantCommand aCommand) {
        Tenant tenant = this.existingTenant(aCommand.getTenantId());

        tenant.deactivate();
    }

    @Transactional
    public void changeUserContactInformation(ChangeContactInfoCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        this.internalChangeUserContactInformation(
                user,
                new ContactInformation(
                        new EmailAddress(aCommand.getEmailAddress()),
                        new PostalAddress(
                                aCommand.getAddressStreetAddress(),
                                aCommand.getAddressCity(),
                                aCommand.getAddressStateProvince(),
                                aCommand.getAddressPostalCode(),
                                aCommand.getAddressCountryCode()),
                        new Telephone(aCommand.getPrimaryTelephone()),
                        new Telephone(aCommand.getSecondaryTelephone())));
    }

    @Transactional
    public void changeUserEmailAddress(ChangeEmailAddressCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        this.internalChangeUserContactInformation(
                user,
                user.person()
                    .contactInformation()
                    .changeEmailAddress(new EmailAddress(aCommand.getEmailAddress())));
    }

    @Transactional
    public void changeUserPostalAddress(ChangePostalAddressCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        this.internalChangeUserContactInformation(
                user,
                user.person()
                    .contactInformation()
                    .changePostalAddress(
                            new PostalAddress(
                                    aCommand.getAddressStreetAddress(),
                                    aCommand.getAddressCity(),
                                    aCommand.getAddressStateProvince(),
                                    aCommand.getAddressPostalCode(),
                                    aCommand.getAddressCountryCode())));
    }

    @Transactional
    public void changeUserPrimaryTelephone(ChangePrimaryTelephoneCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        this.internalChangeUserContactInformation(
                user,
                user.person()
                    .contactInformation()
                    .changePrimaryTelephone(new Telephone(aCommand.getTelephone())));
    }

    @Transactional
    public void changeUserSecondaryTelephone(ChangeSecondaryTelephoneCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        this.internalChangeUserContactInformation(
                user,
                user.person()
                    .contactInformation()
                    .changeSecondaryTelephone(new Telephone(aCommand.getTelephone())));
    }

    @Transactional
    public void changeUserPassword(ChangeUserPasswordCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        user.changePassword(aCommand.getCurrentPassword(), aCommand.getChangedPassword());
    }

    @Transactional
    public void changeUserPersonalName(ChangeUserPersonalNameCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        user.person().changeName(new FullName(aCommand.getFirstName(), aCommand.getLastName()));
    }

    @Transactional
    public void defineUserEnablement(DefineUserEnablementCommand aCommand) {
        User user = this.existingUser(aCommand.getTenantId(), aCommand.getUsername());

        user.defineEnablement(
                new Enablement(
                        aCommand.isEnabled(),
                        aCommand.getStartDate(),
                        aCommand.getEndDate()));
    }

    @Transactional(readOnly=true)
    public Group group(String aTenantId, String aGroupName) {
        Group group =
                this.groupRepository()
                    .groupNamed(new TenantId(aTenantId), aGroupName);

        return group;
    }

    @Transactional(readOnly=true)
    public boolean isGroupMember(String aTenantId, String aGroupName, String aUsername) {
        Group group =
                this.existingGroup(
                        aTenantId,
                        aGroupName);

        User user =
                this.existingUser(
                        aTenantId,
                        aUsername);

        return group.isMember(user, this.groupMemberService());
    }

    @Transactional
    public Group provisionGroup(ProvisionGroupCommand aCommand) {
        Tenant tenant = this.existingTenant(aCommand.getTenantId());

        Group group =
                tenant.provisionGroup(
                        aCommand.getGroupName(),
                        aCommand.getDescription());

        this.groupRepository().add(group);

        return group;
    }

    @Transactional
    public Tenant provisionTenant(ProvisionTenantCommand aCommand) {

        return
            this.tenantProvisioningService().provisionTenant(
                        aCommand.getTenantName(),
                        aCommand.getTenantDescription(),
                        new FullName(
                                aCommand.getAdministorFirstName(),
                                aCommand.getAdministorLastName()),
                        new EmailAddress(aCommand.getEmailAddress()),
                        new PostalAddress(
                                aCommand.getAddressStateProvince(),
                                aCommand.getAddressCity(),
                                aCommand.getAddressStateProvince(),
                                aCommand.getAddressPostalCode(),
                                aCommand.getAddressCountryCode()),
                        new Telephone(aCommand.getPrimaryTelephone()),
                        new Telephone(aCommand.getSecondaryTelephone()));
    }

    @Transactional
    public User registerUser(RegisterUserCommand aCommand) {
        Tenant tenant = this.existingTenant(aCommand.getTenantId());

        User user =
            tenant.registerUser(
                    aCommand.getInvitationIdentifier(),
                    aCommand.getUsername(),
                    aCommand.getPassword(),
                    new Enablement(
                            aCommand.isEnabled(),
                            aCommand.getStartDate(),
                            aCommand.getEndDate()),
                    new Person(
                            new TenantId(aCommand.getTenantId()),
                            new FullName(aCommand.getFirstName(), aCommand.getLastName()),
                            new ContactInformation(
                                    new EmailAddress(aCommand.getEmailAddress()),
                                    new PostalAddress(
                                            aCommand.getAddressStateProvince(),
                                            aCommand.getAddressCity(),
                                            aCommand.getAddressStateProvince(),
                                            aCommand.getAddressPostalCode(),
                                            aCommand.getAddressCountryCode()),
                                    new Telephone(aCommand.getPrimaryTelephone()),
                                    new Telephone(aCommand.getSecondaryTelephone()))));

        if (user == null) {
            throw new IllegalStateException("User not registered.");
        }

        this.userRepository().add(user);

        return user;
    }

    @Transactional
    public void removeGroupFromGroup(RemoveGroupFromGroupCommand aCommand) {
        Group parentGroup =
                this.existingGroup(
                        aCommand.getTenantId(),
                        aCommand.getParentGroupName());

        Group childGroup =
                this.existingGroup(
                        aCommand.getTenantId(),
                        aCommand.getChildGroupName());

        parentGroup.removeGroup(childGroup);
    }

    @Transactional
    public void removeUserFromGroup(RemoveUserFromGroupCommand aCommand) {
        Group group =
                this.existingGroup(
                        aCommand.getTenantId(),
                        aCommand.getGroupName());

        User user =
                this.existingUser(
                        aCommand.getTenantId(),
                        aCommand.getUsername());

        group.removeUser(user);
    }

    @Transactional(readOnly=true)
    public Tenant tenant(String aTenantId) {
        Tenant tenant =
                this.tenantRepository()
                    .tenantOfId(new TenantId(aTenantId));

        return tenant;
    }

    @Transactional(readOnly=true)
    public User user(String aTenantId, String aUsername) {
        User user =
                this.userRepository()
                    .userWithUsername(
                            new TenantId(aTenantId),
                            aUsername);

        return user;
    }

    @Transactional(readOnly=true)
    public UserDescriptor userDescriptor(
            String aTenantId,
            String aUsername) {

        UserDescriptor userDescriptor = null;

        User user = this.user(aTenantId, aUsername);

        if (user != null) {
            userDescriptor = user.userDescriptor();
        }

        return userDescriptor;
    }

    private AuthenticationService authenticationService() {
        return this.authenticationService;
    }

    private Group existingGroup(String aTenantId, String aGroupName) {
        Group group = this.group(aTenantId, aGroupName);

        if (group == null) {
            throw new IllegalArgumentException(
                    "Group does not exist for: "
                    + aTenantId + " and: " + aGroupName);
        }

        return group;
    }

    private Tenant existingTenant(String aTenantId) {
        Tenant tenant = this.tenant(aTenantId);

        if (tenant == null) {
            throw new IllegalArgumentException(
                    "Tenant does not exist for: " + aTenantId);
        }

        return tenant;
    }

    private User existingUser(String aTenantId, String aUsername) {
        User user = this.user(aTenantId, aUsername);

        if (user == null) {
            throw new IllegalArgumentException(
                    "User does not exist for: "
                    + aTenantId + " and " + aUsername);
        }

        return user;
    }

    private GroupMemberService groupMemberService() {
        return this.groupMemberService;
    }

    private GroupRepository groupRepository() {
        return this.groupRepository;
    }

    private void internalChangeUserContactInformation(
            User aUser,
            ContactInformation aContactInformation) {

        if (aUser == null) {
            throw new IllegalArgumentException("User must exist.");
        }

        aUser.person().changeContactInformation(aContactInformation);
    }

    private TenantProvisioningService tenantProvisioningService() {
        return this.tenantProvisioningService;
    }

    private TenantRepository tenantRepository() {
        return this.tenantRepository;
    }

    private UserRepository userRepository() {
        return this.userRepository;
    }
}
