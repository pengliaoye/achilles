CREATE TABLE `content_news` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `title` varchar(1000) DEFAULT NULL COMMENT '标题',
  `content` varchar(5000)  DEFAULT NULL COMMENT '内容',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_deleted` tinyint(255) unsigned DEFAULT NULL COMMENT '1 是  0 否',
  PRIMARY KEY (`id`)
);

CREATE TABLE system_user (
  id bigserial primary key,
  telephone varchar(11) DEFAULT NULL,
  id_card varchar(61) DEFAULT NULL,
  created_at timestamptz DEFAULT CURRENT_TIMESTAMP,
  updated_at timestamptz DEFAULT NULL
);

