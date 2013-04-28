package com.saasovation.agilepm.application.sprint;

public class CommitBacklogItemToSprintCommand {

    private String backlogItemId;
    private String sprintId;
    private String tenantId;

    public CommitBacklogItemToSprintCommand(
            String tenantId,
            String sprintId,
            String backlogItemId) {

        super();

        this.backlogItemId = backlogItemId;
        this.sprintId = sprintId;
        this.tenantId = tenantId;
    }

    public String getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(String backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public String getSprintId() {
        return sprintId;
    }

    public void setSprintId(String sprintId) {
        this.sprintId = sprintId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
}
