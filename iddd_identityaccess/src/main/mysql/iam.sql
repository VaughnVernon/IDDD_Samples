DROP DATABASE IF EXISTS iddd_iam;
CREATE DATABASE iddd_iam;
USE iddd_iam;
SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `tbl_group` (
    `id` bigint(20) NOT NULL auto_increment,
    `description` varchar(250) NOT NULL,
    `name` varchar(100) NOT NULL,
    `tenant_id_id` varchar(36) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    KEY `k_tenant_id_id` (`tenant_id_id`),
    UNIQUE KEY `k_tenant_id_name` (`name`,`tenant_id_id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_group_member` (
    `id` bigint(20) NOT NULL auto_increment,
    `name` varchar(100) NOT NULL,
    `tenant_id_id` varchar(36) NOT NULL,
    `type` varchar(5) NOT NULL,
    `group_id` bigint(20) NOT NULL,
    KEY `k_group_id` (`group_id`),
    KEY `k_tenant_id_id` (`tenant_id_id`),
    CONSTRAINT `fk_tbl_group_member_tbl_group` FOREIGN KEY (`group_id`) REFERENCES `tbl_group` (`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_person` (
    -- primary key is my parent's pk, which is table 'tbl_user'
    `id` bigint(20) NOT NULL,
    `contact_information_email_address_address` varchar(100) NOT NULL,
    `contact_information_postal_address_city` varchar(100) NOT NULL,
    `contact_information_postal_address_country_code` varchar(2) NOT NULL,
    `contact_information_postal_address_postal_code` varchar(12) NOT NULL,
    `contact_information_postal_address_state_province` varchar(100) NOT NULL,
    `contact_information_postal_address_street_address` varchar(100),
    `contact_information_primary_telephone_number` varchar(20) NOT NULL,
    `contact_information_secondary_telephone_number` varchar(20) NOT NULL,
    `name_first_name` varchar(50) NOT NULL,
    `name_last_name` varchar(50) NOT NULL,
    `tenant_id_id` varchar(36) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    KEY `k_tenant_id_id` (`tenant_id_id`),
    CONSTRAINT `fk_tbl_person_tbl_user` FOREIGN KEY (`id`) REFERENCES `tbl_user` (`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_registration_invitation` (
    `id` bigint(20) NOT NULL auto_increment,
    `description` varchar(100) NOT NULL,
    `invitation_id` varchar(36) NOT NULL,
    `starting_on` datetime,
    `tenant_id_id` varchar(36) NOT NULL,
    `until` datetime,
    `tenant_id` bigint(20) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    KEY `k_tenant_id` (`tenant_id`),
    KEY `k_tenant_id_id` (`tenant_id_id`),
    UNIQUE KEY `k_invitation_id` (`invitation_id`),
    CONSTRAINT `fk_tbl_registration_invitation_tbl_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tbl_tenant` (`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_role` (
    `id` bigint(20) NOT NULL auto_increment,
    `description` varchar(250) NOT NULL,
    `group_id` bigint(20) NOT NULL,
    `name` varchar(100) NOT NULL,
    `supports_nesting` tinyint(1) NOT NULL,
    `tenant_id_id` varchar(36) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    KEY `k_tenant_id_id` (`tenant_id_id`),
    UNIQUE KEY `k_tenant_id_name` (`name`,`tenant_id_id`),
    CONSTRAINT `fk_tbl_role_tbl_group` FOREIGN KEY (`group_id`) REFERENCES `tbl_group` (`id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_tenant` (
    `id` bigint(20) NOT NULL auto_increment,
    `active` tinyint(1) NOT NULL,
    `description` varchar(100),
    `name` varchar(100) NOT NULL,
    `tenant_id_id` varchar(36) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    UNIQUE KEY `k_name` (`name`),
    UNIQUE KEY `k_tenant_id_id` (`tenant_id_id`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;

CREATE TABLE `tbl_user` (
    `id` bigint(20) NOT NULL auto_increment,
    `enablement_enabled` tinyint(1) NOT NULL,
    `enablement_end_date` datetime,
    `enablement_start_date` datetime,
    `password` varchar(32) NOT NULL,
    `tenant_id_id` varchar(36) NOT NULL,
    `username` varchar(250) NOT NULL,
    `concurrency_version` int(11) NOT NULL,
    KEY `k_tenant_id_id` (`tenant_id_id`),
    UNIQUE KEY `k_tenant_id_username` (`tenant_id_id`,`username`),
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;
