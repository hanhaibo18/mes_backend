-- drop database IF EXISTS mes_system;
CREATE SCHEMA mes_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE mes_system;

-- -----------------------------------------------------
-- Table `mes_system`.`sys_log`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mes_system`.`sys_log` ;
CREATE TABLE IF NOT EXISTS `mes_system`.`sys_log` (
  `id` VARCHAR(32) NOT NULL,
  `tenant_id` VARCHAR(32) NOT NULL COMMENT '所属租户',
  `type` varchar(32) DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `service_id` varchar(32) DEFAULT NULL,
  `remote_addr` VARCHAR(255) DEFAULT NULL,
  `user_agent` VARCHAR(1000) DEFAULT NULL,
  `request_uri` VARCHAR(255) DEFAULT NULL,
  `method` VARCHAR(10) DEFAULT NULL,
  `params` text COMMENT '操作内容',
  `result` char(1) NOT NULL COMMENT '操作结果',
  `time` mediumtext CHARACTER SET utf8 COMMENT '方法执行时间',
  `exception` text,
  `create_by` VARCHAR(32) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `modify_by` VARCHAR(32) NOT NULL,
  `modify_time` DATETIME NOT NULL DEFAULT NOW(),
  `remark` VARCHAR(200) NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `sys_log_create_by` (`create_by`) USING BTREE,
  KEY `sys_log_request_uri` (`request_uri`) USING BTREE,
  KEY `sys_log_type` (`type`) USING BTREE,
  KEY `sys_log_create_date` (`create_time`) USING BTREE
)COMMENT = '系统日志';

-- -----------------------------------------------------
-- Table `mes_system`.`sys_menu`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mes_system`.`sys_menu` ;

CREATE TABLE IF NOT EXISTS `mes_system`.`sys_menu` (
  `id` VARCHAR(32) NOT NULL,
  `menu_name` VARCHAR(45) NOT NULL COMMENT '名称',
  `menu_type` TINYINT(4) NOT NULL COMMENT '类型',
  `open_type` TINYINT(4) NOT NULL COMMENT '打开方式',
  `menu_url` VARCHAR(255) NOT NULL COMMENT '链接',
  `menu_icon` VARCHAR(45) NULL COMMENT '菜单图标',
  `menu_order` INT NULL,
  `menu_show` VARCHAR(255) NULL COMMENT '菜单显示URL',
  `permission` VARCHAR(32) NULL COMMENT '菜单权限标识',
  `parent_id` VARCHAR(32) NOT NULL,
  `create_by` VARCHAR(32) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `modify_by` VARCHAR(32) NOT NULL,
  `modify_time` DATETIME NOT NULL DEFAULT NOW(),
  `remark` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
COMMENT = '系统菜单';


-- -----------------------------------------------------
-- Table `mes_system`.`sys_tenant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mes_system`.`sys_tenant` ;

CREATE TABLE IF NOT EXISTS `mes_system`.`sys_tenant` (
  `id` VARCHAR(32) NOT NULL COMMENT '租户ID',
  `tenant_name` VARCHAR(128) NOT NULL COMMENT '租户姓名',
  `tenant_name_for_short` VARCHAR(45) NULL COMMENT '租户名称简称',
  `tenant_code` VARCHAR(128) NOT NULL COMMENT '租户标识',
  `tenant_status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '当前状态',
  `tenant_desc` VARCHAR(200) NULL COMMENT '描述',
  `tenant_addr` VARCHAR(200) NULL COMMENT '地址',
  `tenant_contact` VARCHAR(32) NULL COMMENT '联系人',
  `tenant_tel` VARCHAR(32) NULL COMMENT '联系人电话',
  `tenant_mail` VARCHAR(45) NULL COMMENT '联系人邮件',
  `create_by` VARCHAR(32) NOT NULL,
  `create_time` DATETIME NOT NULL  DEFAULT NOW(),
  `modify_by` VARCHAR(32) NOT NULL,
  `modify_time` DATETIME NOT NULL  DEFAULT NOW(),
  `remark` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
COMMENT = '租户';


-- -----------------------------------------------------
-- Table `mes_system`.`sys_tenant_menu`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mes_system`.`sys_tenant_menu` ;

CREATE TABLE IF NOT EXISTS `mes_system`.`sys_tenant_menu` (
  `id` VARCHAR(32) NOT NULL,
  `tenant_id` VARCHAR(32) NOT NULL COMMENT '租户id',
  `menu_id` VARCHAR(32) NOT NULL COMMENT '菜单id',
  `create_by` VARCHAR(32) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT NOW(),
  `modify_by` VARCHAR(32) NOT NULL,
  `modify_time` DATETIME NOT NULL DEFAULT NOW(),
  `remark` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
COMMENT = '租户菜单';


-- -----------------------------------------------------
-- Table `mes_system`.`sys_tenant_user`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mes_system`.`sys_tenant_user` ;

CREATE TABLE IF NOT EXISTS `mes_system`.`sys_tenant_user` (
  `id` VARCHAR(32) NOT NULL,
  `user_type` TINYINT(4) NOT NULL COMMENT '用户类型',
  `user_account` VARCHAR(32) NOT NULL COMMENT '账号',
  `passwd` VARCHAR(100) NOT NULL COMMENT '密码',
  `tenant_id` VARCHAR(32) NOT NULL COMMENT '租户ID',
  `telephone` VARCHAR(32) NULL COMMENT '手机',
  `mail` VARCHAR(100) NULL COMMENT '邮箱',
  `status` TINYINT(4) NOT NULL DEFAULT 0 COMMENT '账号状态',
  `epml_name` VARCHAR(32) NULL COMMENT '员工姓名',
  `org_id` VARCHAR(32) NULL COMMENT '二级单位',
  `belong_org_id` VARCHAR(32) NULL COMMENT '所在结构ID',
  `create_by` VARCHAR(32) NOT NULL,
  `create_time` DATETIME NOT NULL DEFAULT now(),
  `modify_by` VARCHAR(32) NOT NULL,
  `modify_time` DATETIME NOT NULL DEFAULT now(),
  `remark` VARCHAR(200) NULL,
  PRIMARY KEY (`id`))
COMMENT = '租户用户';

-- ----------------------------
-- Table structure for `mes_system`.`sys_role`
-- ----------------------------
DROP TABLE IF EXISTS `mes_system`.`sys_role`;
CREATE TABLE `mes_system`.`sys_role` (
    `id` VARCHAR(32) NOT NULL COMMENT '主键',
    `role_name` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '角色名称',
    `role_code` VARCHAR(128) NOT NULL DEFAULT '' COMMENT '角色code',
    `role_desc` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '角色描述',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '启用禁用状态 1-启用，0-禁用',
    `tenant_id` VARCHAR(32) NOT NULL COMMENT '租户id',
    `create_by` VARCHAR(32) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT NOW(),
    `modify_by` VARCHAR(32) NOT NULL,
    `modify_time` DATETIME NOT NULL DEFAULT NOW(),
    `remark` VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
)  COMMENT='角色表';

-- ----------------------------
-- Table structure for  `mes_system`.`sys_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `mes_system`.`sys_user_role`;
CREATE TABLE `mes_system`.`sys_user_role` (
    `id` VARCHAR(32) NOT NULL,
    `user_id` VARCHAR(32) NOT NULL,
    `role_id` VARCHAR(32) NOT NULL,
    `create_by` VARCHAR(32) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT NOW(),
    `modify_by` VARCHAR(32) NOT NULL,
    `modify_time` DATETIME NOT NULL DEFAULT NOW(),
    `remark` VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
)  COMMENT='用户角色关系表';

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `id` VARCHAR(32) NOT NULL,
    `role_id` VARCHAR(32) NOT NULL COMMENT '角色ID',
    `menu_id` VARCHAR(32) NOT NULL COMMENT '菜单ID',
    `create_by` VARCHAR(32) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT NOW(),
    `modify_by` VARCHAR(32) NOT NULL,
    `modify_time` DATETIME NOT NULL DEFAULT NOW(),
    `remark` VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
) COMMENT='角色菜单表';

-- ----------------------------
-- Table structure for `mes_system`.`sys_attachment`
-- ----------------------------
DROP TABLE IF EXISTS `sys_attachment`;
CREATE TABLE `sys_attachment` (
    `id` VARCHAR(32) NOT NULL,
    `attach_name` VARCHAR(255) NULL DEFAULT NULL COMMENT '附件名称',
    `attach_type` VARCHAR(128) NULL DEFAULT NULL COMMENT '附件类型',
    `attach_size` VARCHAR(255) NULL DEFAULT NULL COMMENT '附件大小',
    `group_name` VARCHAR(255) NULL DEFAULT NULL COMMENT '组名称',
    `fast_file_id` VARCHAR(255) NULL DEFAULT NULL COMMENT '文件ID',
    `module` VARCHAR(255) NULL DEFAULT NULL COMMENT '业务模块',
    `preview_url` VARCHAR(255) NULL DEFAULT NULL COMMENT '预览地址',
    `tenant_id` VARCHAR(32) NOT NULL COMMENT '租户id',
    `create_by` VARCHAR(32) NOT NULL,
    `create_time` DATETIME NOT NULL DEFAULT NOW(),
    `modify_by` VARCHAR(32) NOT NULL,
    `modify_time` DATETIME NOT NULL DEFAULT NOW(),
    `remark` VARCHAR(200) NULL,
    PRIMARY KEY (`id`)
)  COMMENT='附件表';

-- ----------------------------
-- Records of sys_tenant
-- ----------------------------
INSERT INTO `sys_tenant` VALUES ('12345678901234567890000000000000','system','system','system',0,'system','system','system','12345678901','test@test.com','system',now(),'system',now(),'');
INSERT INTO `sys_tenant` VALUES ('12345678901234567890123456789000','mes','mes','mes',0,'mes','mes','mes','12345678901','test@test.com','admin',now(),'admin',now(),'');

-- ----------------------------
-- Records of sys_tenant_user
-- ----------------------------
INSERT INTO `sys_tenant_user` VALUES ('02345678901234567890000000000001', 0, 'system', '$2a$10$S4KyJj9wIUo2KgzfADv98.gqPQHotKg6LQKCSS/qDcqmNrgebMYsq', '12345678901234567890000000000000', '12345678901', 'test@test.com', true, 'admin', '', '', 'admin', now(), 'admin', now(), '');
INSERT INTO `sys_tenant_user` VALUES ('02345678901234567890123456789001', 0, 'admin', '$2a$10$S4KyJj9wIUo2KgzfADv98.gqPQHotKg6LQKCSS/qDcqmNrgebMYsq', '12345678901234567890123456789000', '12345678901', 'test@test.com', true, 'admin', '', '', 'admin', now(), 'admin', now(), '');
INSERT INTO `sys_tenant_user` VALUES ('02345678901234567890123456789002', 0, 'tenant01', '$2a$10$S4KyJj9wIUo2KgzfADv98.gqPQHotKg6LQKCSS/qDcqmNrgebMYsq', '12345678901234567890123456789000', '12345678901', 'test@test.com', true, 'tenant01', '', '', 'admin', now(), 'admin', now(), '');
INSERT INTO `sys_tenant_user` VALUES ('02345678901234567890123456789003', 0, 'zhangsan', '$2a$10$S4KyJj9wIUo2KgzfADv98.gqPQHotKg6LQKCSS/qDcqmNrgebMYsq', '12345678901234567890123456789000', '12345678901', 'test@test.com', true, 'zhangsan', '', '', 'admin', now(), 'admin', now(), '');

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (12345678901234567890000000000000, '超级管理员', 'role_admin', '超级管理员', 1, '12345678901234567890000000000000','admin', now(), 'admin', now(),'');
INSERT INTO `sys_role` VALUES (12345678901234567890123456789001, '超级管理员', 'role_admin', '超级管理员', 1, '12345678901234567890123456789000','admin', now(), 'admin', now(),'');
INSERT INTO `sys_role` VALUES (12345678901234567890123456789002, '租户管理员', 'role_tenant_admin', '租户管理员',  1,'12345678901234567890123456789000', 'admin', now(), 'admin', now(),'');
INSERT INTO `sys_role` VALUES (12345678901234567890123456789003, '普通用户', 'role_user', '普通用户', 1,'12345678901234567890123456789000', 'admin', now(), 'admin', now(),'');

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('22345678901234567890000000000000', '02345678901234567890000000000001', '12345678901234567890000000000000', 'system', now(), 'system', now(),'');
INSERT INTO `sys_user_role` VALUES ('22345678901234567890123456789001', '02345678901234567890123456789001', '12345678901234567890123456789001', 'admin', now(), 'admin', now(),'');
INSERT INTO `sys_user_role` VALUES ('22345678901234567890123456789002', '02345678901234567890123456789002', '12345678901234567890123456789002', 'admin', now(), 'admin', now(),'');
INSERT INTO `sys_user_role` VALUES ('22345678901234567890123456789003', '02345678901234567890123456789003', '12345678901234567890123456789003', 'admin', now(), 'admin', now(),'');

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('12345678901234567890000000000001', '12345678901234567890000000000000', '4657a1cc26702bb1d9a53b76219d66a6', 'system', now(), 'system', now(),'');
INSERT INTO `sys_role_menu` VALUES ('12345678901234567890000000000002', '12345678901234567890000000000000', 'ed486fec2636d86a0b72c8411c3180ac', 'system', now(), 'system', now(),'');
INSERT INTO `sys_role_menu` VALUES ('12345678901234567890000000000003', '12345678901234567890000000000000', 'cbf1138a99d094875f4e8f38dfec9bd5', 'system', now(), 'system', now(),'');
INSERT INTO `sys_role_menu` VALUES ('12345678901234567890000000000004', '12345678901234567890000000000000', '2bfff163120810dec1b1b321c759c80c',  'system', now(), 'system', now(),'');