DROP TABLE IF EXISTS `lexicon`;

CREATE TABLE `lexicon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `term` varchar(200) DEFAULT NULL,
  `qualification` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `level_2_lexicon`;

CREATE TABLE `level_2_lexicon` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `primaryTest` varchar(200) DEFAULT NULL,
  `secondaryTest` varchar(200) DEFAULT NULL,
  `categoryLevel1` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `topic`;

CREATE TABLE `topic` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  `keywords` varchar(300) DEFAULT NULL,
  `status` int(11) DEFAULT 0,
  `ctime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `mtime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `cuser` varchar(100) DEFAULT NULL,
  `muser` varchar(100) DEFAULT NULL,
  `company` bigint(20) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `stop_words`;

CREATE TABLE `stop_words` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `word` varchar(50) DEFAULT NULL,
  `language` CHAR(5) DEFAULT 'en',
  `description` varchar(200) DEFAULT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `black_words`;

CREATE TABLE `black_words` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `word` varchar(50) DEFAULT NULL,
  `language` CHAR(5) DEFAULT 'en',
  `description` varchar(200) DEFAULT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `popular_tweets`;

CREATE TABLE `popular_tweets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL,
  `tweet_id` bigint(20) NOT NULL,
  `text` varchar(560) DEFAULT NULL,
  `from_user_id` bigint(20) DEFAULT NULL,
  `from_user` varchar(50) DEFAULT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `profile_image_url_https` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `retweets_count` int(11) DEFAULT '0',
  `recent_retweets_count` int(11) DEFAULT '0',
  `followers_sum` int(11) DEFAULT '0',
  `estimated_reach` int(11) DEFAULT '0',
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_tweet_id` (`tweet_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_from_user_id` (`from_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `intention_tweets`;

CREATE TABLE `intention_tweets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL,
  `tweet_id` bigint(20) NOT NULL,
  `text` varchar(560) DEFAULT NULL,
  `from_user_id` bigint(20) DEFAULT NULL,
  `from_user` varchar(50) DEFAULT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `profile_image_url_https` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `retweets_count` int(11) DEFAULT '0',
  `recent_retweets_count` int(11) DEFAULT '0',
  `followers_sum` int(11) DEFAULT '0',
  `estimated_reach` int(11) DEFAULT '0',
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_tweet_id` (`tweet_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_from_user_id` (`from_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `keyword_intention_tweets`;

CREATE TABLE `keyword_intention_tweets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL,
  `tweet_id` bigint(20) NOT NULL,
  `text` varchar(560) DEFAULT NULL,
  `from_user_id` bigint(20) DEFAULT NULL,
  `from_user` varchar(50) DEFAULT NULL,
  `profile_image_url` varchar(255) DEFAULT NULL,
  `profile_image_url_https` varchar(255) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `retweets_count` int(11) DEFAULT '0',
  `recent_retweets_count` int(11) DEFAULT '0',
  `followers_sum` int(11) DEFAULT '0',
  `estimated_reach` int(11) DEFAULT '0',
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_tweet_id` (`tweet_id`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_from_user_id` (`from_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `agg_tweets_by_topics`;

DROP TABLE IF EXISTS `topic_mentions`;

CREATE TABLE `topic_mentions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL,
  `adatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `tweets_count` int(11) NOT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_adatetime` (`adatetime`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;


DROP TABLE IF EXISTS `agg_terms_by_topics`;

DROP TABLE IF EXISTS `topic_terms`;

CREATE TABLE `topic_terms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL ,
  `adatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `term` char(50) NOT NULL ,
  `terms_count` int(11) NOT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_adatetime` (`adatetime`),
  KEY `idx_term` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;


DROP TABLE IF EXISTS `topic_bi_terms`;

CREATE TABLE `topic_bi_terms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL,
  `adatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `term` char(100) NOT NULL,
  `terms_count` int(11) NOT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_adatetime` (`adatetime`),
  KEY `idx_term` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;

DROP TABLE IF EXISTS `topic_tri_terms`;

CREATE TABLE `topic_tri_terms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `topic_id` bigint(20) NOT NULL,
  `adatetime` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  `term` char(150) NOT NULL,
  `terms_count` int(11) NOT NULL,
  `insert_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modification_date` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',
  PRIMARY KEY (`id`),
  KEY `idx_topic_id` (`topic_id`),
  KEY `idx_adatetime` (`adatetime`),
  KEY `idx_term` (`term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 DEFAULT COLLATE utf8_general_ci;


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

