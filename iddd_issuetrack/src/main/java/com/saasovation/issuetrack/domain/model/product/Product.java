package com.saasovation.issuetrack.domain.model.product;

import static com.google.common.collect.Sets.newHashSet;
import static com.saasovation.issuetrack.domain.model.product.issue.IssueId.newIssueId;
import static com.saasovation.issuetrack.domain.model.product.issue.IssueType.DEFECT;
import static com.saasovation.issuetrack.domain.model.product.issue.IssueType.FEATURE;

import java.util.Set;

import com.saasovation.issuetrack.domain.model.IssueAssigner;
import com.saasovation.issuetrack.domain.model.ProductManager;
import com.saasovation.issuetrack.domain.model.product.issue.Issue;
import com.saasovation.issuetrack.domain.model.product.issue.IssueType;
import com.saasovation.issuetrack.domain.model.tenant.TenantId;

public class Product {


    private ProductId productId;

    private TenantId tenantId;
    private String name;
    private String description;
    private ProductManager productManager;
    private IssueAssigner issueAssigner;
    private Set<Issue> issues = newHashSet();

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

    public Issue reportDefect(String description, String summary) {
	return createIssue(description, summary, DEFECT);
    }

    public Issue requestFeature(String description, String summary) {
	return createIssue(description, summary, FEATURE);
    }

    private Issue createIssue(String description, String summary, IssueType type) {
	Issue issue = new Issue(newIssueId(), tenantId, description, summary, type);
	issues.add(issue);
	return issue;
    }

}
