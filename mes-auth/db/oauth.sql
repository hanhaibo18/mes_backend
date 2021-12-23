
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
-- DROP DATABASE IF EXISTS mes_oauth;
-- CREATE DATABASE mes_oauth DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE mes_oauth;
-- ----------------------------
-- Table structure for oauth_client_details
-- ----------------------------
DROP TABLE IF EXISTS oauth_client_details;
CREATE TABLE oauth_client_details (
    `id` VARCHAR(32) NOT NULL,
    `client_id` VARCHAR(64) NOT NULL COMMENT '客户端ID',
    `resource_ids` VARCHAR(255) COMMENT '资源ID集合,多个资源时用逗号(,)分隔',
    `client_secret_plain_text` varchar(255) COMMENT '密钥明文',
    `client_secret` VARCHAR(255) COMMENT '客户端密匙',
    `scope` VARCHAR(255) COMMENT '客户端申请的权限范围',
    `authorized_grant_types` VARCHAR(255) COMMENT '客户端支持的grant_type',
    `web_server_redirect_uri` VARCHAR(255) COMMENT '重定向URI',
    `authorities` VARCHAR(255) COMMENT '客户端所拥有的Spring Security的权限值，多个用逗号(,)分隔',
    `access_token_validity` INTEGER COMMENT '访问令牌有效时间值(单位:秒)',
    `refresh_token_validity` INTEGER COMMENT '更新令牌有效时间值(单位:秒)',
    `additional_information` VARCHAR(4096) COMMENT '预留字段',
    `autoapprove` VARCHAR(255) COMMENT '用户是否自动Approval操作',
    `create_by` VARCHAR(32) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT NOW(),
    `modify_by` VARCHAR(32) NOT NULL,
    `modify_time` DATETIME NOT NULL DEFAULT NOW(),
    `remark` VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
)  COMMENT '客户端信息';

-- ----------------------------
-- Records of oauth_client_details
-- ----------------------------
INSERT INTO `oauth_client_details` VALUES ('12345678901234567890123456789012', 'web_app', NULL, 'mes-web-secret','$2a$10$Pjk5B/J8Sv.cS8bkNUV0gOznS84LsPoqUhChOqhDHYfBtAgikKNIi', 'read,write','password,authorization_code,refresh_token,implicit', NULL, NULL, '86400', '86400', NULL, NULL, 'admin', now(), 'admin', now(),'');

SET FOREIGN_KEY_CHECKS = 1;
