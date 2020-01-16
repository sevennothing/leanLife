-- 创建用户
CREATE TABLE `user` (
  `userId` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `name` varchar(100) NOT NULL COMMENT '用户名',
  `password` varchar(20) NOT NULL COMMENT '密码',
  `secretQuestion` varchar(20) NOT NULL COMMENT '密保问题',
  `secretAnswer` varchar(20) NOT NULL COMMENT '密保答案',
  `registerDate` date NOT NULL COMMENT '注册时间',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='用户表';

-- 创建用户配置表
CREATE TABLE `userConfig` (
  `userId` bigint(20) NOT NULL COMMENT '用户ID',
  `dataPath` varchar(100) COMMENT '数据存储路径',
  PRIMARY KEY (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户配置表';

-- Sqlite  数据库
CREATE TABLE `user` (
  `name` varchar(100) NOT NULL UNIQUE,
  `password` varchar(20) NOT NULL ,
  `secretQuestionId` int NOT NULL,
  `secretAnswer` varchar(100) NOT NULL ,
  `registerDate` date NOT NULL ,
  PRIMARY KEY (`name`)
);


-- 创建资源信息表
CREATE TABLE `pictureData` (
  `name` varchar(100) NOT NULL COMMENT '图片名称',
  `createTime` date NOT NULL COMMENT '创建时间',
  `modifyTime` date NOT NULL COMMENT '修改时间',
  `resolution` varchar(100) NOT NULL COMMENT '图片分辨率',
  `hDPI` int NOT NULL COMMENT '水平分辨率',
  `vDPI` int NOT NULL COMMENT '垂直分辨率',
  `bitDepth` int NOT NULL COMMENT '位深度',
  `size` int NOT NULL COMMENT '大小',
  `photoTime` date COMMENT '拍摄时间',
  PRIMARY KEY (`name`)
);

