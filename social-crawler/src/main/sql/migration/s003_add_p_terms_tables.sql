DROP TABLE IF EXISTS `topic_terms_p`;

CREATE TABLE `topic_terms_p` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL ,
  `adatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `period` smallint(2) NOT NULL ,
  `term` char(50) NOT NULL ,
  `terms_count` int(11) NOT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_adatetime` (`adatetime`),
  key `idx_period` (`period`),
  KEY `idx_term` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `topic_bi_terms_p`;

CREATE TABLE `topic_bi_terms_p` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL ,
  `adatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `period` smallint(2) NOT NULL ,
  `term` char(100) NOT NULL ,
  `terms_count` int(11) NOT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_adatetime` (`adatetime`),
  key `idx_period` (`period`),
  KEY `idx_term` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `topic_tri_terms_p`;

CREATE TABLE `topic_tri_terms_p` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL ,
  `adatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `period` smallint(2) NOT NULL ,
  `term` char(150) NOT NULL ,
  `terms_count` int(11) NOT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_adatetime` (`adatetime`),
  key `idx_period` (`period`),
  KEY `idx_term` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;