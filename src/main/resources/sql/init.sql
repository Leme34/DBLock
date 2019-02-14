-- 假设有一个场景，有一个catalog商品目录表，然后还有一个browse浏览表
-- 假如一个商品被浏览了，那么就需要记录下浏览的user是谁，并且记录访问的总数。

-- int(11)可以简写为int，默认是长度是11位数，也是mysql中int型的最大可以表示的位数，占4字节，在java中需要Long类型才能存储（参照Java数据类型和MySql数据类型对应表）

CREATE TABLE catalog(
id INT(11) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
name VARCHAR(50) NOT NULL DEFAULT '' COMMENT '商品名称',
browse_count int(11) NOT NULL DEFAULT 0 COMMENT '浏览数',
version int(11) NOT NULL DEFAULT 0 COMMENT '乐观锁的版本号'
)charset=utf8;

CREATE TABLE browse(
id INT(11) UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
cata_id int(11) NOT NULL COMMENT '商品ID',
user VARCHAR(50) NOT NULL COMMENT '浏览用户',
create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间'
)charset=utf8;

insert into catalog (name, browse_count, version) values('mycatalog', 0, 0);
