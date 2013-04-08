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

import java.util.Iterator;

public class GroupMemberService {

    private GroupRepository groupRepository;
    private UserRepository userRepository;

    public GroupMemberService(
            UserRepository aUserRepository,
            GroupRepository aGroupRepository) {

        super();

        this.groupRepository = aGroupRepository;
        this.userRepository = aUserRepository;
    }

    public boolean confirmUser(Group aGroup, User aUser) {
        boolean userConfirmed = true;

        User confirmedUser =
                this.userRepository()
                    .userWithUsername(aGroup.tenantId(), aUser.username());

        if (confirmedUser == null || !confirmedUser.isEnabled()) {
            userConfirmed = false;
        }

        return userConfirmed;
    }

    public boolean isMemberGroup(Group aGroup, GroupMember aMemberGroup) {
        boolean isMember = false;

        Iterator<GroupMember> iter =
            aGroup.groupMembers().iterator();

        while (!isMember && iter.hasNext()) {
            GroupMember member = iter.next();
            if (member.isGroup()) {
                if (aMemberGroup.equals(member)) {
                    isMember = true;
                } else {
                    Group group =
                        this.groupRepository()
                            .groupNamed(member.tenantId(), member.name());
                    if (group != null) {
                        isMember = this.isMemberGroup(group, aMemberGroup);
                    }
                }
            }
        }

        return isMember;
    }

    public boolean isUserInNestedGroup(Group aGroup, User aUser) {
        boolean isInNestedGroup = false;

        Iterator<GroupMember> iter =
            aGroup.groupMembers().iterator();

        while (!isInNestedGroup && iter.hasNext()) {
            GroupMember member = iter.next();
            if (member.isGroup()) {
                Group group =
                        this.groupRepository()
                            .groupNamed(member.tenantId(), member.name());
                if (group != null) {
                    isInNestedGroup = group.isMember(aUser, this);
                }
            }
        }

        return isInNestedGroup;
    }

    private GroupRepository groupRepository() {
        return this.groupRepository;
    }

    private UserRepository userRepository() {
        return this.userRepository;
    }
}
