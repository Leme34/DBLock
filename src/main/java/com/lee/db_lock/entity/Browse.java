package com.lee.db_lock.entity;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * 用户浏览商品的记录表（逆向生成后简化）
 */
@Builder
@Data
@Entity
public class Browse {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //自动生成主键必须加上此注解
    private Long id;

    @Basic
    @Column(name = "cata_id", nullable = false)
    private Long cataId;

    @Basic
    @Column(name = "user", nullable = false)
    private String user;

    @Basic
    @Column(name = "create_time", nullable = false)
    private Timestamp createTime;

}
