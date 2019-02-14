package com.lee.db_lock.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

/**
 * 商品目录表（逆向生成后简化）
 */
@Data
@Entity
public class Catalog {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //自动生成主键必须加上此注解
    private Long id;

    @Basic
    @Column(name = "name", nullable = false, length = 50)
    private String name;  //商品名称

    @Basic
    @Column(name = "browse_count", nullable = false)
    private Long browseCount;  //浏览数

    @Basic
    @Column(name = "version", nullable = false)
    @Version //乐观锁方式2
    private Long version;  //乐观锁版本号

}
