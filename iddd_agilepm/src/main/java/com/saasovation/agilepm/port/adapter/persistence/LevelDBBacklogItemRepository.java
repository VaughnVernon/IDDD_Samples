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
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItem;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemId;
import com.saasovation.agilepm.domain.model.product.backlogitem.BacklogItemRepository;
import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.product.sprint.SprintId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.port.adapter.persistence.leveldb.AbstractLevelDBRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBKey;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBBacklogItemRepository
        extends AbstractLevelDBRepository
        implements BacklogItemRepository {

    private static final String PRIMARY = "BLI#PK";
    private static final String PRODUCT_BACKLOG_ITEMS = "BLI#PROD";
    private static final String RELEASE_BACKLOG_ITEMS = "BLI#RELEASE";
    private static final String SPRINT_BACKLOG_ITEMS = "BLI#SPRINT";
//    private static final String BACKLOG_ITEM_OF_DISCUSSION = "BLI#D";

    public LevelDBBacklogItemRepository() {
        super(LevelDBDatabasePath.agilePMPath());
    }

    @Override
    public Collection<BacklogItem> allBacklogItemsComittedTo(TenantId aTenantId, SprintId aSprintId) {
        List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();

        LevelDBKey sprintBacklogItems = new LevelDBKey(SPRINT_BACKLOG_ITEMS, aTenantId.id(), aSprintId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(sprintBacklogItems);

        for (Object backlogItemId : keys) {
            BacklogItem backlogItem = uow.readObject(backlogItemId.toString().getBytes(), BacklogItem.class);

            if (backlogItem != null) {
                backlogItems.add(backlogItem);
            }
        }

        return backlogItems;
    }

    @Override
    public Collection<BacklogItem> allBacklogItemsScheduledFor(TenantId aTenantId, ReleaseId aReleaseId) {
        List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();

        LevelDBKey releaseBacklogItems = new LevelDBKey(RELEASE_BACKLOG_ITEMS, aTenantId.id(), aReleaseId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(releaseBacklogItems);

        for (Object backlogItemId : keys) {
            BacklogItem backlogItem = uow.readObject(backlogItemId.toString().getBytes(), BacklogItem.class);

            if (backlogItem != null) {
                backlogItems.add(backlogItem);
            }
        }

        return backlogItems;
    }

    @Override
    public Collection<BacklogItem> allOutstandingProductBacklogItems(TenantId aTenantId, ProductId aProductId) {
        List<BacklogItem> productBacklogItems = this.listProductBacklogItems(aTenantId, aProductId);

        Iterator<BacklogItem> iterator = productBacklogItems.listIterator();

        if (iterator.hasNext()) {
            BacklogItem backlogItem = iterator.next();

            if (backlogItem.isDone() || backlogItem.isRemoved()) {
                iterator.remove();
            }
        }

        return productBacklogItems;
    }

    @Override
    public Collection<BacklogItem> allProductBacklogItems(TenantId aTenantId, ProductId aProductId) {
        return this.listProductBacklogItems(aTenantId, aProductId);
    }

    @Override
    public BacklogItem backlogItemOfId(TenantId aTenantId, BacklogItemId aBacklogItemId) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTenantId.id(), aBacklogItemId.id());

        BacklogItem backlogItem =
                LevelDBUnitOfWork.readOnly(this.database())
                    .readObject(primaryKey.key().getBytes(), BacklogItem.class);

        return backlogItem;
    }

    @Override
    public BacklogItemId nextIdentity() {
        return new BacklogItemId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void remove(BacklogItem aBacklogItem) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aBacklogItem.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.remove(aBacklogItem, uow);
    }

    @Override
    public void removeAll(Collection<BacklogItem> aBacklogItemCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (BacklogItem backlogItem : aBacklogItemCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, backlogItem.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.remove(backlogItem, uow);
        }
    }

    @Override
    public void save(BacklogItem aBacklogItem) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aBacklogItem.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.save(aBacklogItem, uow);
    }

    @Override
    public void saveAll(Collection<BacklogItem> aBacklogItemCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (BacklogItem backlogItem : aBacklogItemCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, backlogItem.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.save(backlogItem, uow);
        }
    }

    private List<BacklogItem> listProductBacklogItems(TenantId aTenantId, ProductId aProductId) {
        List<BacklogItem> backlogItems = new ArrayList<BacklogItem>();

        LevelDBKey productBacklogItems = new LevelDBKey(PRODUCT_BACKLOG_ITEMS, aTenantId.id(), aProductId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(productBacklogItems);

        for (Object backlogItemId : keys) {
            BacklogItem backlogItem = uow.readObject(backlogItemId.toString().getBytes(), BacklogItem.class);

            if (backlogItem != null) {
                backlogItems.add(backlogItem);
            }
        }

        return backlogItems;
    }

    private void remove(BacklogItem aBacklogItem, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aBacklogItem.tenantId().id(), aBacklogItem.backlogItemId().id());
        aUoW.remove(primaryKey);

        LevelDBKey productBacklogItems = new LevelDBKey(primaryKey, PRODUCT_BACKLOG_ITEMS, aBacklogItem.tenantId().id(), aBacklogItem.productId().id());
        aUoW.removeKeyReference(productBacklogItems);

//        if (aBacklogItem.discussionInitiationId() != null) {
//            LevelDBKey backlogItemsOfDiscussion = new LevelDBKey(primaryKey, BACKLOG_ITEM_OF_DISCUSSION, aBacklogItem.tenantId().id(), aBacklogItem.discussionInitiationId());
//            aUoW.removeKeyReference(backlogItemsOfDiscussion);
//        }
    }

    private void save(BacklogItem aBacklogItem, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aBacklogItem.tenantId().id(), aBacklogItem.backlogItemId().id());
        aUoW.write(primaryKey, aBacklogItem);

        LevelDBKey productBacklogItems = new LevelDBKey(primaryKey, PRODUCT_BACKLOG_ITEMS, aBacklogItem.tenantId().id(), aBacklogItem.productId().id());
        aUoW.updateKeyReference(productBacklogItems);

        if (aBacklogItem.isScheduledForRelease()) {
            LevelDBKey releaseBacklogItems = new LevelDBKey(primaryKey, RELEASE_BACKLOG_ITEMS, aBacklogItem.tenantId().id(), aBacklogItem.releaseId().id());
            aUoW.updateKeyReference(releaseBacklogItems);

        }

        if (aBacklogItem.isCommittedToSprint()) {
            LevelDBKey sprintBacklogItems = new LevelDBKey(primaryKey, SPRINT_BACKLOG_ITEMS, aBacklogItem.tenantId().id(), aBacklogItem.sprintId().id());
            aUoW.updateKeyReference(sprintBacklogItems);
        }

        // RELEASE_BACKLOG_ITEMS

//        if (aBacklogItem.discussionInitiationId() != null) {
//            LevelDBKey backlogItemsOfDiscussion = new LevelDBKey(primaryKey, BACKLOG_ITEM_OF_DISCUSSION, aBacklogItem.tenantId().id(), aBacklogItem.discussionInitiationId());
//            aUoW.updateKeyReference(backlogItemsOfDiscussion);
//        }
    }
}
