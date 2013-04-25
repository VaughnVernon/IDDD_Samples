package com.saasovation.issuetrack.domain.model.product.issue;

import com.saasovation.common.domain.model.DomainEvent;
import com.saasovation.common.domain.model.Version;

@Version(3)
public class IssueAssociatedWithExistingBacklogItem extends DomainEvent {

    private String issueId;
    private String productId;
    private String tenantId;
    private String description;
    private String summary;

    private String backlogItemId;

    public IssueAssociatedWithExistingBacklogItem(String issueId,
	    String productId, String tenantId, String description, String summary,
	    String backlogItemId) {
	super();
	this.issueId = issueId;
	this.productId = productId;
	this.tenantId = tenantId;
	this.description = description;
	this.summary = summary;
	this.backlogItemId = backlogItemId;
    }

}
