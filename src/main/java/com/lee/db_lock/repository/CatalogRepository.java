package com.lee.db_lock.repository;

import com.lee.db_lock.entity.Catalog;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface CatalogRepository extends CrudRepository<Catalog, Long> {

    /**
     * 悲观锁方式1：自行写原生SQL,然后写上for update语句
     * 一定要加上索引字段的where条件才会是行级锁，否则是表级锁
     */
    @Query(value = "select * from Catalog where id = :id for update",
            nativeQuery = true)
    Optional<Catalog> findCatalogForUpdate(@Param("id") Long id);

    /**
     * 悲观锁方式2：非原生SQL,使用jpa的@Lock注解,设置悲观写锁（排他锁）。
     * 一定要加上索引字段的where条件才会是行级锁，否则是表级锁
     */
    @Lock(value = LockModeType.PESSIMISTIC_WRITE) //此处代表行级锁
    @Query(value = "select c from Catalog c where c.id = :id")  //非原生SQL此处的Catalog是entity
    Optional<Catalog> findCatalogWithPessimisticLock(@Param("id") Long id);

    /**
     * 乐观锁方式1：更新的时候将version字段传过来,更新的时候通过原生SQL判断version,若version匹配才更新
     * 乐观锁方式2：在实体类上的version字段上加入@Version，可以不用自己写SQL语句就可以它就可以自行的按照version匹配和更新
     * @param id 商品id
     * @param browseCount 被浏览次数
     * @param version 传入sql的版本号
     * @return 受影响行数
     */
    @Transactional
    @Modifying(clearAutomatically = true) //清除Hibernate默认的一级缓存(session)，修改时必须加上，否则每次查询都是缓存中的旧数据
    @Query(value = "update catalog set browse_count = :browseCount, version = version + 1 where id = :id and version = :version",
            nativeQuery = true)
    int updateCatalogWithVersion(@Param("id") Long id, @Param("browseCount") Long browseCount, @Param("version") Long version);

}
