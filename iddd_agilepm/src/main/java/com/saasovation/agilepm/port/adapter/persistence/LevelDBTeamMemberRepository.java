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
import java.util.Collection;
import java.util.List;

import com.saasovation.agilepm.domain.model.team.TeamMember;
import com.saasovation.agilepm.domain.model.team.TeamMemberRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.port.adapter.persistence.leveldb.AbstractLevelDBRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBKey;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBTeamMemberRepository
        extends AbstractLevelDBRepository
        implements TeamMemberRepository {

    private static final String PRIMARY = "TEAMMEMBER#1";
    private static final String TEAM_MEMBER_OF_TENANT = "TEAMMEMBER#2";

    public LevelDBTeamMemberRepository() {
        super(LevelDBDatabasePath.agilePMPath());
    }

    @Override
    public Collection<TeamMember> allTeamMembersOfTenant(TenantId aTenantId) {
        List<TeamMember> teamMembers = new ArrayList<TeamMember>();

        LevelDBKey teamMembersOfTenant = new LevelDBKey(TEAM_MEMBER_OF_TENANT, aTenantId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(teamMembersOfTenant);

        for (Object teamMemberId : keys) {
            TeamMember teamMember = uow.readObject(teamMemberId.toString().getBytes(), TeamMember.class);

            if (teamMember != null) {
                teamMembers.add(teamMember);
            }
        }

        return teamMembers;
    }

    @Override
    public void remove(TeamMember aTeamMember) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aTeamMember.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.remove(aTeamMember, uow);
    }

    @Override
    public void removeAll(Collection<TeamMember> aTeamMemberCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (TeamMember teamMember : aTeamMemberCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, teamMember.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.remove(teamMember, uow);
        }
    }

    @Override
    public void save(TeamMember aTeamMember) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aTeamMember.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.save(aTeamMember, uow);
    }

    @Override
    public void saveAll(Collection<TeamMember> aTeamMemberCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (TeamMember teamMember : aTeamMemberCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, teamMember.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.save(teamMember, uow);
        }
    }

    @Override
    public TeamMember teamMemberOfIdentity(TenantId aTenantId, String aUsername) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTenantId.id(), aUsername);

        TeamMember teamMember =
                LevelDBUnitOfWork.readOnly(this.database())
                    .readObject(primaryKey.key().getBytes(), TeamMember.class);

        return teamMember;
    }

    private void remove(TeamMember aTeamMember, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTeamMember.tenantId().id(), aTeamMember.username());
        aUoW.remove(primaryKey);

        LevelDBKey teamMemberOfTenant = new LevelDBKey(primaryKey, TEAM_MEMBER_OF_TENANT, aTeamMember.tenantId().id());
        aUoW.removeKeyReference(teamMemberOfTenant);
    }

    private void save(TeamMember aTeamMember, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTeamMember.tenantId().id(), aTeamMember.username());
        aUoW.write(primaryKey, aTeamMember);

        LevelDBKey teamMembersOfTenant = new LevelDBKey(primaryKey, TEAM_MEMBER_OF_TENANT, aTeamMember.tenantId().id());
        aUoW.updateKeyReference(teamMembersOfTenant);
    }
}
