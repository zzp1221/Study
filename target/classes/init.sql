CREATE DATABASE test
CREATE TABLE user(
                     id BIGINT PRIMARY KEY AUTO_INCREMENT,
                     username VARCHAR(25) not NULL UNIQUE,
                     password  BIGINT not null,
                     authority VARCHAR(25) DEFAULT('user'),
                     deleteFlag int DEFAULT(0)
)
