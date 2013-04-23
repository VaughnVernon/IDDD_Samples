package com.saasovation.issuetrack.domain.model.product;

import org.apache.commons.lang.NotImplementedException;

import com.saasovation.issuetrack.domain.model.IssueAssigner;
import com.saasovation.issuetrack.domain.model.ProductManager;
import com.saasovation.issuetrack.domain.model.issue.Defect;
import com.saasovation.issuetrack.domain.model.issue.Feature;
import com.saasovation.issuetrack.domain.model.tenant.TenantId;

public class Product {


    private TenantId tenantId;
    private ProductId productId;
    private String name;
    private String description;
    private ProductManager productManager;
    private IssueAssigner issueAssigner;

    public static ProductBuilder aProduct() {
	return new ProductBuilder();
    }

    Product(TenantId tenantId, ProductId productId, String name, String description, ProductManager productManager, IssueAssigner issueAssigner) {
	this.tenantId = tenantId;
	this.productId = productId;
	this.name = name;
	this.description = description;
	this.productManager = productManager;
	this.issueAssigner = issueAssigner;
    }

    public Defect reportDefect() {
	throw new NotImplementedException();
    }

    public Feature requestFeature() {
	throw new NotImplementedException();
    }

}
