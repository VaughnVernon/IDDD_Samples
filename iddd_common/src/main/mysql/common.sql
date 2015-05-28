-- =============================================================
-- =============================================================
-- =============================================================
-- TO REUSE IN ANOTHER BOUNDED CONTEXT, APPEND THIS FILE TO
-- THE END OF YOUR DDL
-- =============================================================
-- =============================================================
-- =============================================================

USE iddd_common_test;

CREATE TABLE `tbl_es_event_store` (
    `event_id` bigint(20) NOT NULL auto_increment,
    `event_body` TEXT NOT NULL,
    `event_type` varchar(250) NOT NULL,
    `stream_name` varchar(250) NOT NULL,
    `stream_version` int(11) NOT NULL,
    KEY (`stream_name`),
    UNIQUE KEY (`stream_name`, `stream_version`),
    PRIMARY KEY (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_published_notification_tracker` (
    `published_notification_tracker_id` bigint(20) NOT NULL auto_increment,
    `most_recent_published_notification_id` bigint(20) NOT NULL,
    `type_name` varchar(100) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    PRIMARY KEY (`published_notification_tracker_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_stored_event` (
    `event_id` bigint(20) NOT NULL auto_increment,
    `event_body` TEXT NOT NULL,
    `occurred_on` datetime NOT NULL,
    `type_name` varchar(200) NOT NULL,
    PRIMARY KEY (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_time_constrained_process_tracker` (
    `time_constrained_process_tracker_id` bigint(20) NOT NULL auto_increment,
    `allowable_duration` bigint(20) NOT NULL,
    `completed` tinyint(1) NOT NULL,
    `description` varchar(100) NOT NULL,
    `process_id_id` varchar(36) NOT NULL,
    `process_informed_of_timeout` tinyint(1) NOT NULL,
    `process_timed_out_event_type` varchar(200) NOT NULL,
    `retry_count` int(11) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    `timeout_occurs_on` bigint(20) NOT NULL,
    `total_retries_permitted` bigint(20) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    KEY `k_process_id` (`process_id_id`),
    KEY `k_tenant_id` (`tenant_id`),
    KEY `k_timeout_occurs_on` (`timeout_occurs_on`),
    PRIMARY KEY (`time_constrained_process_tracker_id`)
) ENGINE=InnoDB;
