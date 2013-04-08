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

package com.saasovation.agilepm.port.adapter.persistence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.iq80.leveldb.DB;

import com.saasovation.agilepm.domain.model.team.TeamMember;
import com.saasovation.agilepm.domain.model.team.TeamMemberRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBProvider;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBTeamMemberRepositoryTest extends TestCase {

    private DB database;
    private TeamMemberRepository teamMemberRepository = new LevelDBTeamMemberRepository();

    public LevelDBTeamMemberRepositoryTest() {
        super();
    }


    public void testSave() throws Exception {
        TeamMember teamMember =
                new TeamMember(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(this.database);
        teamMemberRepository.save(teamMember);
        LevelDBUnitOfWork.current().commit();

        TeamMember savedTeamMember =
                teamMemberRepository.teamMemberOfIdentity(
                        teamMember.tenantId(),
                        teamMember.username());

        assertNotNull(savedTeamMember);
        assertEquals(teamMember.tenantId(), savedTeamMember.tenantId());
        assertEquals(teamMember.username(), savedTeamMember.username());
        assertEquals(teamMember.firstName(), savedTeamMember.firstName());
        assertEquals(teamMember.lastName(), savedTeamMember.lastName());
        assertEquals(teamMember.emailAddress(), savedTeamMember.emailAddress());

        Collection<TeamMember> savedTeamMembers =
                this.teamMemberRepository.allTeamMembersOfTenant(teamMember.tenantId());

        assertFalse(savedTeamMembers.isEmpty());
        assertEquals(1, savedTeamMembers.size());
    }

    public void testRemove() {
        TeamMember teamMember1 =
                new TeamMember(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        TeamMember teamMember2 =
                new TeamMember(
                        new TenantId("12345"),
                        "zdoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(this.database);
        teamMemberRepository.save(teamMember1);
        teamMemberRepository.save(teamMember2);
        LevelDBUnitOfWork.current().commit();

        LevelDBUnitOfWork.start(this.database);
        teamMemberRepository.remove(teamMember1);
        LevelDBUnitOfWork.current().commit();

        TenantId tenantId = teamMember2.tenantId();

        Collection<TeamMember> savedTeamMembers = teamMemberRepository.allTeamMembersOfTenant(tenantId);
        assertFalse(savedTeamMembers.isEmpty());
        assertEquals(1, savedTeamMembers.size());
        assertEquals(teamMember2.username(), savedTeamMembers.iterator().next().username());

        LevelDBUnitOfWork.start(this.database);
        teamMemberRepository.remove(teamMember2);
        LevelDBUnitOfWork.current().commit();

        savedTeamMembers = teamMemberRepository.allTeamMembersOfTenant(tenantId);
        assertTrue(savedTeamMembers.isEmpty());
    }

    public void testSaveAllRemoveAll() throws Exception {
        TeamMember teamMember1 =
                new TeamMember(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        TeamMember teamMember2 =
                new TeamMember(
                        new TenantId("12345"),
                        "zdoe",
                        "Zoe",
                        "Doe",
                        "zoe@saasovation.com",
                        new Date());

        TeamMember teamMember3 =
                new TeamMember(
                        new TenantId("12345"),
                        "jsmith",
                        "John",
                        "Smith",
                        "jsmith@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(this.database);
        teamMemberRepository.saveAll(Arrays.asList(new TeamMember[] { teamMember1, teamMember2, teamMember3 }));
        LevelDBUnitOfWork.current().commit();

        TenantId tenantId = teamMember1.tenantId();

        Collection<TeamMember> savedTeamMembers = teamMemberRepository.allTeamMembersOfTenant(tenantId);
        assertFalse(savedTeamMembers.isEmpty());
        assertEquals(3, savedTeamMembers.size());

        LevelDBUnitOfWork.start(this.database);
        teamMemberRepository.removeAll(Arrays.asList(new TeamMember[] { teamMember1, teamMember3 }));
        LevelDBUnitOfWork.current().commit();

        savedTeamMembers = teamMemberRepository.allTeamMembersOfTenant(tenantId);
        assertFalse(savedTeamMembers.isEmpty());
        assertEquals(1, savedTeamMembers.size());
        assertEquals(teamMember2.username(), savedTeamMembers.iterator().next().username());

        LevelDBUnitOfWork.start(this.database);
        teamMemberRepository.removeAll(Arrays.asList(new TeamMember[] { teamMember2 }));
        LevelDBUnitOfWork.current().commit();

        savedTeamMembers = teamMemberRepository.allTeamMembersOfTenant(tenantId);
        assertTrue(savedTeamMembers.isEmpty());
    }

    public void testConcurrentTransactions() throws Exception {
        final List<Integer> orderOfCommits = new ArrayList<Integer>();

        TeamMember teamMember1 =
                new TeamMember(
                        new TenantId("12345"),
                        "jdoe",
                        "John",
                        "Doe",
                        "jdoe@saasovation.com",
                        new Date());

        LevelDBUnitOfWork.start(database);
        teamMemberRepository.save(teamMember1);

        new Thread() {
           @Override
           public void run() {
               TeamMember teamMember2 =
                       new TeamMember(
                               new TenantId("12345"),
                               "zdoe",
                               "Zoe",
                               "Doe",
                               "zoe@saasovation.com",
                               new Date());

               LevelDBUnitOfWork.start(database);
               teamMemberRepository.save(teamMember2);
               LevelDBUnitOfWork.current().commit();
               orderOfCommits.add(2);
           }
        }.start();

        Thread.sleep(250L);

        LevelDBUnitOfWork.current().commit();
        orderOfCommits.add(1);

        for (int idx = 0; idx < orderOfCommits.size(); ++idx) {
            assertEquals(idx + 1, orderOfCommits.get(idx).intValue());
        }

        Thread.sleep(250L);

        Collection<TeamMember> savedTeamMembers =
                teamMemberRepository.allTeamMembersOfTenant(teamMember1.tenantId());

        assertFalse(savedTeamMembers.isEmpty());
        assertEquals(2, savedTeamMembers.size());
    }

    @Override
    protected void setUp() throws Exception {
        DomainEventPublisher.instance().reset();

        this.database = LevelDBProvider.instance().databaseFrom(LevelDBDatabasePath.agilePMPath());

        LevelDBProvider.instance().purge(this.database);

        super.setUp();
    }
}
