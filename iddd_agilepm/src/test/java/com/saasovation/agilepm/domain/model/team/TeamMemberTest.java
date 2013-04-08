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

import java.util.Date;

import com.saasovation.agilepm.domain.model.tenant.TenantId;

public class TeamMemberTest extends TeamCommonTest {

    public TeamMemberTest() {
        super();
    }

    public void testCreate() throws Exception {
        TeamMember teamMember =
                new TeamMember(
                        new TenantId("T-12345"),
                        "bill",
                        "Bill",
                        "Smith",
                        "bill@saasovation.com",
                        new Date());

        assertNotNull(teamMember);

        this.teamMemberRepository.save(teamMember);

        assertEquals("bill", teamMember.username());
        assertEquals("Bill", teamMember.firstName());
        assertEquals("Smith", teamMember.lastName());
        assertEquals("bill@saasovation.com", teamMember.emailAddress());
        assertEquals(teamMember.username(), teamMember.teamMemberId().id());
    }

    public void testChangeEmailAddress() throws Exception {
        TeamMember teamMember = this.teamMemberForTest();

        assertFalse(teamMember.emailAddress().equals("billsmith@saasovation.com"));

        // later...
        Date notificationOccurredOn = new Date();

        teamMember.changeEmailAddress("billsmith@saasovation.com", notificationOccurredOn);

        assertEquals("billsmith@saasovation.com", teamMember.emailAddress());
    }

    public void testChangeName() throws Exception {
        TeamMember teamMember = this.teamMemberForTest();

        assertFalse(teamMember.lastName().equals("Gates"));

        // later...
        Date notificationOccurredOn = new Date();

        teamMember.changeName("Bill", "Gates", notificationOccurredOn);

        assertEquals("Bill", teamMember.firstName());
        assertEquals("Gates", teamMember.lastName());
    }

    public void testDisable() throws Exception {
        TeamMember teamMember = this.teamMemberForTest();

        assertTrue(teamMember.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        teamMember.disable(notificationOccurredOn);

        assertFalse(teamMember.isEnabled());
    }

    public void testEnable() throws Exception {
        TeamMember teamMember = this.teamMemberForTest();

        teamMember.disable(this.twoHoursEarlierThanNow());

        assertFalse(teamMember.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        teamMember.enable(notificationOccurredOn);

        assertTrue(teamMember.isEnabled());
    }

    public void testDisallowEarlierDisabling() {
        TeamMember teamMember = this.teamMemberForTest();

        teamMember.disable(this.twoHoursEarlierThanNow());

        assertFalse(teamMember.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        teamMember.enable(notificationOccurredOn);

        assertTrue(teamMember.isEnabled());

        // latent notification...
        teamMember.disable(this.twoMinutesEarlierThanNow());

        assertTrue(teamMember.isEnabled());
    }

    public void testDisallowEarlierEnabling() {
        TeamMember teamMember = this.teamMemberForTest();

        assertTrue(teamMember.isEnabled());

        // later...
        Date notificationOccurredOn = new Date();

        teamMember.disable(notificationOccurredOn);

        assertFalse(teamMember.isEnabled());

        // latent notification...
        teamMember.enable(this.twoMinutesEarlierThanNow());

        assertFalse(teamMember.isEnabled());
    }
}
