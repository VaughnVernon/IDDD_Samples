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

package com.saasovation.agilepm.domain.model.product.release;

import java.util.Collection;

import com.saasovation.agilepm.domain.model.product.ProductId;
import com.saasovation.agilepm.domain.model.tenant.TenantId;

public interface ReleaseRepository {

    public Collection<Release> allProductReleases(TenantId aTenantId, ProductId aProductId);

    public ReleaseId nextIdentity();

    public Release releaseOfId(TenantId aTenantId, ReleaseId aReleaseId);

    public void remove(Release aRelease);

    public void removeAll(Collection<Release> aReleaseCollection);

    public void save(Release aRelease);

    public void saveAll(Collection<Release> aReleaseCollection);
}
