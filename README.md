The project **S3 SFTP Server** allows you to connect run your SFTP server backed up by S3 storage.

Logs everything to a database (MySQL)

## How to use

Listing parameters from ssm: 

```
aws ssm get-parameters-by-path --path /sftp --with-decryption --recursive --region ca-central-1
```

Creating bcrypted passwords:

```
htpasswd -bnBC 12 "" 'PLAINTEXT PASSWORD HERE!' | tr -d ':\n' | sed 's/$2y/$2a/'
```

The setup is quite easy and it requires you to only edit application.yaml configuration file.

To directly start the app run:
```maven
AWS_REGION="ca-central-1" AWS_ACCESS_KEY_ID="ACCESSKEYHERE" AWS_SECRET_ACCESS_KEY="SECRETKEYHERE" mvn spring-boot:run
```

To produce executable jar:

```maven
mvn clean install -DskipTests
```

Running the app
```
AWS_REGION="ca-central-1" AWS_ACCESS_KEY_ID="ACCESSKEYHERE" AWS_SECRET_ACCESS_KEY="SECRETKEYHERE" java -jar target/s3-sftp-1.0.1-SNAPSHOT.jar
```

### How to configure your app

Example configuration:

```yaml
logging:
  level:
    root: info

app:
  sftp:
    port: 2222 # Sftp server port
    base-folder: "/tmp/test-sftp"
    users: []
    aws:
      bucket-name: "{ssmParameter}/sftp/s3/bucket-name"
    hostkey-algorithm: "RSA"
    hostkey-private: "{ssmParameter}/sftp/sshd/hostkey-private"

server:
  port: 8090

spring:
  application:
    name: sftpDataExchange
  datasource:
    platform: h2
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: "{ssmParameter}/sftp/database/jdbc-connection"
    username: "{ssmParameter}/sftp/database/username"
    password: "{ssmParameter}/sftp/database/password"
    tomcat:
      min-idle: 1
      max-idle: 2
      max-active: 15
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
  jpa:
    open-in-view: false
    show-sql: false
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        cache:
          use_second_level_cache: false
  jackson:
    serialization:
      write_dates_as_timestamps: false

management:
  context-path: /manage
  security:
    enabled: false

security:
  basic:
    enabled: false
```

Database Schema:

```
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
```

Dockerizing

```
docker build -t sftp
docker run -e AWS_REGION="ca-central-1" -e AWS_ACCESS_KEY_ID="ACCESSKEYHERE" -e AWS_SECRET_ACCESS_KEY="SECRETKEYHERE" -p 2222:2222 -p 8090:8090 sftp
```