CREATE TABLE `auth_users` (
  `au_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `au_password` varchar(60) NOT NULL,
  `au_username` varchar(31) NOT NULL,
  PRIMARY KEY (`au_id`),
  UNIQUE KEY `UK_ikowttl8sgo307j8ueais4afq` (`au_username`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `file_events` (
  `fe_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fe_action` varchar(255) DEFAULT NULL,
  `fe_date_finished` datetime DEFAULT NULL,
  `fe_date_started` datetime NOT NULL,
  `fe_filename` varchar(255) NOT NULL,
  `fe_au_id` bigint(20) NOT NULL,
  PRIMARY KEY (`fe_id`),
  KEY `FKl4ptuh9kr9qd40m10isuadduy` (`fe_au_id`),
  CONSTRAINT `FKl4ptuh9kr9qd40m10isuadduy` FOREIGN KEY (`fe_au_id`) REFERENCES `auth_users` (`au_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;