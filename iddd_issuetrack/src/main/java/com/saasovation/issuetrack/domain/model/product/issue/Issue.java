package com.saasovation.issuetrack.domain.model.product.issue;

import org.apache.commons.lang.NotImplementedException;

import com.saasovation.issuetrack.domain.model.tenant.TenantId;

public class Issue {

    private IssueId issueId;
    private TenantId tenantId;
    private String description;
    private String summary;
    private IssueType issueType;



    public Issue(IssueId issueId, TenantId tenantId, String description,
	    String summary, IssueType issueType) {
	this.issueId = issueId;
	this.tenantId = tenantId;
	this.description = description;
	this.summary = summary;
	this.issueType = issueType;
    }

    public void assign() {
	throw new NotImplementedException();
    }

    public void reject() {
	throw new NotImplementedException();
    }

    public void markAsDuplicate() {
	throw new NotImplementedException();
    }

}
