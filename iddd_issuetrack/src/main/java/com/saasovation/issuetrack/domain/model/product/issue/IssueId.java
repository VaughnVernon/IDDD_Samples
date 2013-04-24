package com.saasovation.issuetrack.domain.model.product.issue;

import static java.util.UUID.randomUUID;

import java.util.UUID;

import com.saasovation.common.domain.model.Id;

public class IssueId extends Id {

    public static IssueId newIssueId() {
	return issueId(randomUUID());
    }

    public static IssueId issueId(UUID value) {
	return new IssueId(value);
    }

    private IssueId(UUID value) {
	super(value);
    }

}
