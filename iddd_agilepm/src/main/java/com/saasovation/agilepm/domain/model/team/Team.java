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

package com.saasovation.agilepm.domain.model.team;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.saasovation.agilepm.domain.model.Entity;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class Team extends Entity {

    private String name;
    private ProductOwner productOwner;
    private Set<TeamMember> teamMembers;
    private TenantId tenantId;

    public Team(TenantId aTenantId, String aName, ProductOwner aProductOwner) {
        this();

        this.setName(aName);
        this.setProductOwner(aProductOwner);
        this.setTenantId(aTenantId);
    }

    public Team(TenantId aTenantId, String aName) {
        this();

        this.setName(aName);
        this.setTenantId(aTenantId);
    }

    public Set<TeamMember> allTeamMembers() {
        return Collections.unmodifiableSet(this.teamMembers());
    }

    public void assignProductOwner(ProductOwner aProductOwner) {
        this.assertArgumentEquals(this.tenantId(), aProductOwner.tenantId(), "Product owner must be of the same tenant.");

        this.setProductOwner(aProductOwner);
    }

    public void assignTeamMember(TeamMember aTeamMember) {
        this.assertArgumentEquals(this.tenantId(), aTeamMember.tenantId(), "Team member must be of the same tenant.");

        this.teamMembers().add(aTeamMember);
    }

    public String name() {
        return this.name;
    }

    public boolean isTeamMember(TeamMember aTeamMember) {
        this.assertArgumentEquals(this.tenantId(), aTeamMember.tenantId(), "Team member must be of the same tenant.");

        boolean isTeamMember = false;
        String usernameToMatch = aTeamMember.username();

        for (TeamMember member : this.teamMembers()) {
            if (member.username().equals(usernameToMatch)) {
                isTeamMember = true;
                break;
            }
        }

        return isTeamMember;
    }

    public ProductOwner productOwner() {
        return this.productOwner;
    }

    public void removeTeamMember(TeamMember aTeamMember) {
        this.assertArgumentEquals(this.tenantId(), aTeamMember.tenantId(), "Team member must be of the same tenant.");

        TeamMember memberToRemove = null;
        String usernameToMatch = aTeamMember.username();

        for (TeamMember member : this.teamMembers()) {
            if (member.username().equals(usernameToMatch)) {
                memberToRemove = member;
                break;
            }
        }

        this.teamMembers().remove(memberToRemove);
    }

    public TenantId tenantId() {
        return this.tenantId;
    }

    @Override
    public boolean equals(Object anObject) {
        boolean equalObjects = false;

        if (anObject != null && this.getClass() == anObject.getClass()) {
            Team typedObject = (Team) anObject;
            equalObjects =
                this.tenantId().equals(typedObject.tenantId()) &&
                this.name().equals(typedObject.name());
        }

        return equalObjects;
    }

    @Override
    public int hashCode() {
        int hashCodeValue =
            + (63815 * 59)
            + this.tenantId().hashCode()
            + this.name().hashCode();

        return hashCodeValue;
    }

    private Team() {
        super();

        this.setTeamMembers(new HashSet<TeamMember>(0));
    }

    private void setName(String aName) {
        this.assertArgumentNotEmpty(aName, "The name must be provided.");
        this.assertArgumentLength(aName, 100, "The name must be 100 characters or less.");

        this.name = aName;
    }

    private void setProductOwner(ProductOwner aProductOwner) {
        this.assertArgumentNotNull(aProductOwner, "The productOwner must be provided.");
        this.assertArgumentEquals(this.tenantId(), aProductOwner.tenantId(), "The productOwner must be of the same tenant.");

        this.productOwner = aProductOwner;
    }

    private Set<TeamMember> teamMembers() {
        return this.teamMembers;
    }

    private void setTeamMembers(Set<TeamMember> aTeamMembers) {
        this.teamMembers = aTeamMembers;
    }

    private void setTenantId(TenantId aTenantId) {
        this.assertArgumentNotNull(aTenantId, "The tenantId must be provided.");

        this.tenantId = aTenantId;
    }
}
