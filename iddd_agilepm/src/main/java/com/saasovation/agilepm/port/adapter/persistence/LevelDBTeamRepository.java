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

import com.saasovation.agilepm.domain.model.team.Team;
import com.saasovation.agilepm.domain.model.team.TeamRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.port.adapter.persistence.leveldb.AbstractLevelDBRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBKey;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBTeamRepository
        extends AbstractLevelDBRepository
        implements TeamRepository {

    private static final String PRIMARY = "TEAM#1";
    private static final String TEAM_OF_TENANT = "TEAM#2";

    public LevelDBTeamRepository() {
        super(LevelDBDatabasePath.agilePMPath());
    }

    @Override
    public Collection<Team> allTeamsOfTenant(TenantId aTenantId) {
        List<Team> teams = new ArrayList<Team>();

        LevelDBKey teamsOfTenant = new LevelDBKey(TEAM_OF_TENANT, aTenantId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(teamsOfTenant);

        for (Object teamId : keys) {
            Team team = uow.readObject(teamId.toString().getBytes(), Team.class);

            if (team != null) {
                teams.add(team);
            }
        }

        return teams;
    }

    @Override
    public void remove(Team aTeam) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aTeam.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.remove(aTeam, uow);
    }

    @Override
    public void removeAll(Collection<Team> aTeamCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (Team team : aTeamCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, team.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.remove(team, uow);
        }
    }

    @Override
    public void save(Team aTeam) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aTeam.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.save(aTeam, uow);
    }

    @Override
    public void saveAll(Collection<Team> aTeamCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (Team team : aTeamCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, team.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.save(team, uow);
        }
    }

    @Override
    public Team teamNamed(TenantId aTenantId, String aName) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTenantId.id(), aName);

        Team team =
                LevelDBUnitOfWork.readOnly(this.database())
                    .readObject(primaryKey.key().getBytes(), Team.class);

        return team;
    }

    private void remove(Team aTeam, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTeam.tenantId().id(), aTeam.name());
        aUoW.remove(primaryKey);

        LevelDBKey teamOfTenant = new LevelDBKey(primaryKey, TEAM_OF_TENANT, aTeam.tenantId().id());
        aUoW.removeKeyReference(teamOfTenant);
    }

    private void save(Team aTeam, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTeam.tenantId().id(), aTeam.name());
        aUoW.write(primaryKey, aTeam);

        LevelDBKey teamOfTenant = new LevelDBKey(primaryKey, TEAM_OF_TENANT, aTeam.tenantId().id());
        aUoW.updateKeyReference(teamOfTenant);
    }
}
