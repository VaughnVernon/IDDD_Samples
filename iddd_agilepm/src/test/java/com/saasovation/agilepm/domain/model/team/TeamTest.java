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

import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class TeamTest extends TeamCommonTest {

    public void testCreate() throws Exception {
        TenantId tenantId = new TenantId("T-12345");

        Team team = new Team(tenantId, "Identity and Access Management");

        this.teamRepository.save(team);

        assertEquals("Identity and Access Management", team.name());
    }

    public void testAssignProductOwner() throws Exception {
        Team team = this.teamForTest();

        ProductOwner productOwner = this.productOwnerForTest();

        team.assignProductOwner(productOwner);

        assertNotNull(team.productOwner());
        assertEquals(productOwner.productOwnerId(), team.productOwner().productOwnerId());
    }

    public void testAssignTeamMembers() throws Exception {
        Team team = this.teamForTest();

        TeamMember teamMember1 = this.teamMemberForTest1();
        TeamMember teamMember2 = this.teamMemberForTest2();
        TeamMember teamMember3 = this.teamMemberForTest3();

        team.assignTeamMember(teamMember1);
        team.assignTeamMember(teamMember2);
        team.assignTeamMember(teamMember3);

        assertFalse(team.allTeamMembers().isEmpty());
        assertEquals(3, team.allTeamMembers().size());

        assertTrue(team.isTeamMember(teamMember1));
        assertTrue(team.isTeamMember(teamMember2));
        assertTrue(team.isTeamMember(teamMember3));
    }

    public void testRemoveTeamMembers() throws Exception {
        Team team = this.teamForTest();

        TeamMember teamMember1 = this.teamMemberForTest1();
        TeamMember teamMember2 = this.teamMemberForTest2();
        TeamMember teamMember3 = this.teamMemberForTest3();

        team.assignTeamMember(teamMember1);
        team.assignTeamMember(teamMember2);
        team.assignTeamMember(teamMember3);

        assertFalse(team.allTeamMembers().isEmpty());
        assertEquals(3, team.allTeamMembers().size());

        team.removeTeamMember(teamMember2);

        assertFalse(team.allTeamMembers().isEmpty());
        assertEquals(2, team.allTeamMembers().size());

        assertTrue(team.isTeamMember(teamMember1));
        assertFalse(team.isTeamMember(teamMember2));
        assertTrue(team.isTeamMember(teamMember3));
    }
}
