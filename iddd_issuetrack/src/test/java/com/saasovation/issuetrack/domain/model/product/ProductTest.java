package com.saasovation.issuetrack.domain.model.product;

import static com.saasovation.issuetrack.domain.model.product.Product.aProduct;
import static com.saasovation.issuetrack.domain.model.product.ProductId.newProductId;
import static com.saasovation.issuetrack.domain.model.tenant.TenantId.newTenantId;

import org.junit.Test;
import org.mockito.Mock;

import com.saasovation.issuetrack.domain.model.IssueAssigner;
import com.saasovation.issuetrack.domain.model.ProductManager;

public class ProductTest {

    @Mock
    private ProductManager productManager;

    @Mock
    private IssueAssigner issueAssigner;

    @Test
    public void testBuilder() {
	Product product = aProduct().withTenantId(newTenantId())
		.withProductId(newProductId())
		.withName("name")
		.withDescription("description")
		.withProductManager(productManager)
		.withIssueAssigner(issueAssigner);
    }

}
