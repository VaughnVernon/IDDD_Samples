DROP DATABASE IF EXISTS iddd_collaboration;
CREATE DATABASE iddd_collaboration;
USE iddd_collaboration;
SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `tbl_dispatcher_last_event` (
    `event_id` bigint(20) NOT NULL,
    PRIMARY KEY (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_es_event_store` (
    `event_id` bigint(20) NOT NULL auto_increment,
    `event_body` text NOT NULL,
    `event_type` varchar(250) NOT NULL,
    `stream_name` varchar(250) NOT NULL,
    `stream_version` int(11) NOT NULL,
    KEY (`stream_name`),
    UNIQUE KEY (`stream_name`, `stream_version`),
    PRIMARY KEY (`event_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_vw_calendar` (
    `calendar_id` varchar(36) NOT NULL,
    `description` varchar(500),
    `name` varchar(100) NOT NULL,
    `owner_email_address` varchar(100) NOT NULL,
    `owner_identity` varchar(50) NOT NULL,
    `owner_name` varchar(200) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    KEY `k_owner_identity` (`owner_identity`),
    KEY `k_tenant_id` (`name`,`tenant_id`),
    PRIMARY KEY (`calendar_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_vw_calendar_entry` (
    `calendar_entry_id` varchar(36) NOT NULL,
    `alarm_alarm_units` int(11) NOT NULL,
    `alarm_alarm_units_type` varchar(10) NOT NULL,
    `calendar_id` varchar(36) NOT NULL,
    `description` varchar(500),
    `location` varchar(100),
    `owner_email_address` varchar(100) NOT NULL,
    `owner_identity` varchar(50) NOT NULL,
    `owner_name` varchar(200) NOT NULL,
    `repetition_ends` datetime NOT NULL,
    `repetition_type` varchar(20) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    `time_span_begins` datetime NOT NULL,
    `time_span_ends` datetime NOT NULL,
    KEY `k_calendar_id` (`calendar_id`),
    KEY `k_owner_identity` (`owner_identity`),
    KEY `k_repetition_ends` (`repetition_ends`),
    KEY `k_tenant_id` (`tenant_id`),
    KEY `k_time_span_begins` (`time_span_begins`),
    KEY `k_time_span_ends` (`time_span_ends`),
    PRIMARY KEY (`calendar_entry_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_vw_calendar_entry_invitee` (
    `id` int(11) NOT NULL auto_increment,
    `calendar_entry_id` varchar(36) NOT NULL,
    `participant_email_address` varchar(100) NOT NULL,
    `participant_identity` varchar(50) NOT NULL,
    `participant_name` varchar(200) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    KEY `k_calendar_entry_id` (`calendar_entry_id`),
    KEY `k_participant_identity` (`participant_identity`),
    KEY `k_tenant_id` (`tenant_id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_vw_calendar_sharer` (
    `id` int(11) NOT NULL auto_increment,
    `calendar_id` varchar(36) NOT NULL,
    `participant_email_address` varchar(100) NOT NULL,
    `participant_identity` varchar(50) NOT NULL,
    `participant_name` varchar(200) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    KEY `k_calendar_id` (`calendar_id`),
    KEY `k_participant_identity` (`participant_identity`),
    KEY `k_tenant_id` (`tenant_id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_vw_discussion` (
    `discussion_id` varchar(36) NOT NULL,
    `author_email_address` varchar(100) NOT NULL,
    `author_identity` varchar(50) NOT NULL,
    `author_name` varchar(200) NOT NULL,
    `closed` tinyint(1) NOT NULL,
    `exclusive_owner` varchar(100),
    `forum_id` varchar(36) NOT NULL,
    `subject` varchar(100) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    KEY `k_author_identity` (`author_identity`),
    KEY `k_forum_id` (`forum_id`),
    KEY `k_tenant_id` (`tenant_id`),
    KEY `k_exclusive_owner` (`exclusive_owner`),
    PRIMARY KEY (`discussion_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_vw_forum` (
    `forum_id` varchar(36) NOT NULL,
    `closed` tinyint(1) NOT NULL,
    `creator_email_address` varchar(100) NOT NULL,
    `creator_identity` varchar(50) NOT NULL,
    `creator_name` varchar(200) NOT NULL,
    `description` varchar(500) NOT NULL,
    `exclusive_owner` varchar(100),
    `moderator_email_address` varchar(100) NOT NULL,
    `moderator_identity` varchar(50) NOT NULL,
    `moderator_name` varchar(200) NOT NULL,
    `subject` varchar(100) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    KEY `k_creator_identity` (`creator_identity`),
    KEY `k_tenant_id` (`tenant_id`),
    KEY `k_exclusive_owner` (`exclusive_owner`),
    PRIMARY KEY (`forum_id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_vw_post` (
    `post_id` varchar(36) NOT NULL,
    `author_email_address` varchar(100) NOT NULL,
    `author_identity` varchar(50) NOT NULL,
    `author_name` varchar(200) NOT NULL,
    `body_text` text NOT NULL,
    `changed_on` datetime NOT NULL,
    `created_on` datetime NOT NULL,
    `discussion_id` varchar(36) NOT NULL,
    `forum_id` varchar(36) NOT NULL,
    `reply_to_post_id` varchar(36),
    `subject` varchar(100) NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    KEY `k_author_identity` (`author_identity`),
    KEY `k_discussion_id` (`discussion_id`),
    KEY `k_forum_id` (`forum_id`),
    KEY `k_reply_to_post_id` (`reply_to_post_id`),
    KEY `k_tenant_id` (`tenant_id`),
    PRIMARY KEY (`post_id`)
) ENGINE=InnoDB;
