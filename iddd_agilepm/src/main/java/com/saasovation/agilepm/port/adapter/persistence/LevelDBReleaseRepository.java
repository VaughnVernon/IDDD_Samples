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
import java.util.UUID;

import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.product.release.Release;
import com.saasovation.agilepm.domain.model.product.release.ReleaseId;
import com.saasovation.agilepm.domain.model.product.release.ReleaseRepository;
import com.saasovation.agilepm.domain.model.tenant.TenantId;
import com.saasovation.common.port.adapter.persistence.leveldb.AbstractLevelDBRepository;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBKey;
import com.saasovation.common.port.adapter.persistence.leveldb.LevelDBUnitOfWork;

public class LevelDBReleaseRepository
        extends AbstractLevelDBRepository
        implements ReleaseRepository {

    private static final String PRIMARY = "RELEASE#PK";
    private static final String PRODUCT_RELEASES = "RELEASE#PR";

    public LevelDBReleaseRepository() {
        super(LevelDBDatabasePath.agilePMPath());
    }

    @Override
    public Collection<Release> allProductReleases(TenantId aTenantId, ProductId aProductId) {
        List<Release> releases = new ArrayList<Release>();

        LevelDBKey productReleases = new LevelDBKey(PRODUCT_RELEASES, aTenantId.id(), aProductId.id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.readOnly(this.database());

        List<Object> keys = uow.readKeys(productReleases);

        for (Object releaseId : keys) {
            Release release = uow.readObject(releaseId.toString().getBytes(), Release.class);

            if (release != null) {
                releases.add(release);
            }
        }

        return releases;
    }

    @Override
    public ReleaseId nextIdentity() {
        return new ReleaseId(UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public Release releaseOfId(TenantId aTenantId, ReleaseId aReleaseId) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aTenantId.id(), aReleaseId.id());

        Release release =
                LevelDBUnitOfWork.readOnly(this.database())
                    .readObject(primaryKey.key().getBytes(), Release.class);

        return release;
    }

    @Override
    public void remove(Release aRelease) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aRelease.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.remove(aRelease, uow);
    }

    @Override
    public void removeAll(Collection<Release> aReleaseCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (Release release : aReleaseCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, release.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.remove(release, uow);
        }
    }

    @Override
    public void save(Release aRelease) {
        LevelDBKey lockKey = new LevelDBKey(PRIMARY, aRelease.tenantId().id());

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        uow.lock(lockKey.key());

        this.save(aRelease, uow);
    }

    @Override
    public void saveAll(Collection<Release> aReleaseCollection) {
        boolean locked = false;

        LevelDBUnitOfWork uow = LevelDBUnitOfWork.current();

        for (Release release : aReleaseCollection) {
            if (!locked) {
                LevelDBKey lockKey = new LevelDBKey(PRIMARY, release.tenantId().id());

                uow.lock(lockKey.key());

                locked = true;
            }

            this.save(release, uow);
        }
    }

    private void remove(Release aRelease, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aRelease.tenantId().id(), aRelease.releaseId().id());
        aUoW.remove(primaryKey);

        LevelDBKey productReleases = new LevelDBKey(primaryKey, PRODUCT_RELEASES, aRelease.tenantId().id(), aRelease.productId().id());
        aUoW.removeKeyReference(productReleases);
    }

    private void save(Release aRelease, LevelDBUnitOfWork aUoW) {
        LevelDBKey primaryKey = new LevelDBKey(PRIMARY, aRelease.tenantId().id(), aRelease.releaseId().id());
        aUoW.write(primaryKey, aRelease);

        LevelDBKey productReleases = new LevelDBKey(primaryKey, PRODUCT_RELEASES, aRelease.tenantId().id(), aRelease.productId().id());
        aUoW.updateKeyReference(productReleases);
    }
}
