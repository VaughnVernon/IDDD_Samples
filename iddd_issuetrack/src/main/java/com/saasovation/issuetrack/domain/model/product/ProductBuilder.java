package com.saasovation.issuetrack.domain.model.product;

import static com.saasovation.issuetrack.domain.model.product.ProductId.newProductId;

import com.saasovation.issuetrack.domain.model.IssueAssigner;
import com.saasovation.issuetrack.domain.model.ProductManager;
import com.saasovation.issuetrack.domain.model.tenant.TenantId;

class ProductBuilder {

    private TenantId tenantId;
    private ProductId productId;
    private String name;
    private String description;
    private ProductManager productManager;
    private IssueAssigner issueAssigner;

    ProductIdBuilder withTenantId(TenantId tenantId) {
	this.tenantId = tenantId;
	return new ProductIdBuilder();
    }

    final class ProductIdBuilder {
	NameBuilder withNewProductId() {
	    return withProductId(newProductId());
	}

	NameBuilder withProductId(ProductId productId) {
	    ProductBuilder.this.productId = productId;
	    return new NameBuilder();
	}
    }

    final class NameBuilder {
	DescriptionBuilder withName(String name) {
	    ProductBuilder.this.name = name;
	    return new DescriptionBuilder();
	}
    }

    final class DescriptionBuilder {
	ProductManagerBuilder withDescription(String description) {
	    ProductBuilder.this.description = description;
	    return new ProductManagerBuilder();
	}
    }

    final class ProductManagerBuilder {
	IssueAssignerBuilder withProductManager(ProductManager productManager) {
	    ProductBuilder.this.productManager = productManager;
	    return new IssueAssignerBuilder();
	}
    }

    final class IssueAssignerBuilder {
	Product withIssueAssigner(IssueAssigner issueAssigner) {
	    ProductBuilder.this.issueAssigner = issueAssigner;
	    return new Product(tenantId, productId, name, description, productManager, ProductBuilder.this.issueAssigner);
	}
    }

}
