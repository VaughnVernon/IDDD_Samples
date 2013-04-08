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

import com.saasovation.agilepm.domain.model.DomainTest;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public abstract class TeamCommonTest extends DomainTest {

    public TeamCommonTest() {
        super();
    }

    protected Date twoHoursEarlierThanNow() {
        return new Date(new Date().getTime() - (3600000 * 2));
    }

    protected Date twoMinutesEarlierThanNow() {
        return new Date(new Date().getTime() - (1000 * 120));
    }

    protected ProductOwner productOwnerForTest() {
        ProductOwner productOwner =
                new ProductOwner(
                        new TenantId("T-12345"),
                        "zoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date(new Date().getTime() - (86400000L * 30)));

        return productOwner;
    }

    protected Team teamForTest() {
        TenantId tenantId = new TenantId("T-12345");

        Team team = new Team(tenantId, "Identity and Access Management");

        return team;
    }

    protected TeamMember teamMemberForTest() {
        return this.teamMemberForTest1();
    }

    protected TeamMember teamMemberForTest1() {
        TeamMember teamMember =
                new TeamMember(
                        new TenantId("T-12345"),
                        "bill",
                        "Bill",
                        "Smith",
                        "bill@saasovation.com",
                        new Date(new Date().getTime() - (86400000L * 30)));

        return teamMember;
    }

    protected TeamMember teamMemberForTest2() {
        TeamMember teamMember =
                new TeamMember(
                        new TenantId("T-12345"),
                        "zoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date(new Date().getTime() - (86400000L * 30)));

        return teamMember;
    }

    protected TeamMember teamMemberForTest3() {
        TeamMember teamMember =
                new TeamMember(
                        new TenantId("T-12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date(new Date().getTime() - (86400000L * 30)));

        return teamMember;
    }
}
