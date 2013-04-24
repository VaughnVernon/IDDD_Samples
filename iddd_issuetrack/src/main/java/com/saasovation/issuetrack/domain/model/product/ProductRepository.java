package com.saasovation.issuetrack.domain.model.product;

import com.saasovation.issuetrack.domain.model.tenant.TenantId;

public interface ProductRepository {

    Iterable<Product> getAll(TenantId aTenantId);

}
