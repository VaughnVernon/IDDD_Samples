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

package com.saasovation.agilepm.domain.model;

import org.iq80.leveldb.DB;

import com.saasovation.agilepm.domain.model.product.ProductRepository;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemRepository;
import com.saasovation.agilepm.domain.model.product.release.ReleaseRepository;
import com.saasovation.agilepm.domain.model.product.sprint.SprintRepository;
import com.saasovation.agilepm.domain.model.team.ProductOwnerRepository;
import com.saasovation.agilepm.domain.model.team.TeamMemberRepository;
import com.saasovation.agilepm.domain.model.team.TeamRepository;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBBacklogItemRepository;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBDatabasePath;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBProductOwnerRepository;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBProductRepository;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBReleaseRepository;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBSprintRepository;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBTeamMemberRepository;
import com.saasovation.agilepm.port.adapter.persistence.LevelDBTeamRepository;
import com.saasovation.common.domain.model.DomainEventPublisher;
import com.saasovation.common.domain.model.EventTrackingTestCase;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBProvider;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public abstract class DomainTest extends EventTrackingTestCase {

    protected BacklogItemRepository backlogItemRepository;
    protected DB database;
    protected ProductOwnerRepository productOwnerRepository;
    protected ProductRepository productRepository;
    protected ReleaseRepository releaseRepository;
    protected SprintRepository sprintRepository;
    protected TeamMemberRepository teamMemberRepository;
    protected TeamRepository teamRepository;

    public DomainTest() {
        super();
    }

    protected void setUp() throws Exception {

        System.out.println(">>>>>>>>>>>>>>>>>>>> " + this.getName());

        DomainEventPublisher.instance().reset();

        this.database = LevelDBProvider.instance().databaseFrom(LevelDBDatabasePath.agilePMPath());

        LevelDBProvider.instance().purge(this.database);

        LevelDBUnitOfWork.start(this.database);

        this.backlogItemRepository = new LevelDBBacklogItemRepository();
        this.productOwnerRepository = new LevelDBProductOwnerRepository();
        this.productRepository = new LevelDBProductRepository();
        this.releaseRepository = new LevelDBReleaseRepository();
        this.sprintRepository = new LevelDBSprintRepository();
        this.teamMemberRepository = new LevelDBTeamMemberRepository();
        this.teamRepository = new LevelDBTeamRepository();

        super.setUp();
    }

    protected void tearDown() throws Exception {

        System.out.println("<<<<<<<<<<<<<<<<<<<< (done)");

        LevelDBProvider.instance().purge(this.database);

        super.tearDown();
    }
}
