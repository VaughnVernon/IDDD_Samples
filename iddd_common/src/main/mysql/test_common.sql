DROP DATABASE IF EXISTS iddd_common_test;
CREATE DATABASE iddd_common_test;
USE iddd_common_test;
SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE `tbl_testable_time_constrained_process` (
    `id` bigint(20) NOT NULL auto_increment,
    `allowable_duration` bigint(20) NOT NULL,
    `confirm1` tinyint(1) NOT NULL,
    `confirm2` tinyint(1) NOT NULL,
    `description` varchar(200),
    `process_id_id` varchar(36) NOT NULL,
    `process_completion_type` varchar(50) NOT NULL,
    `start_time` datetime NOT NULL,
    `tenant_id` varchar(36) NOT NULL,
    `timed_out_date` datetime,
    `concurrency_version` int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB;
