package com.lee.db_lock.service;

import com.lee.db_lock.annotation.RetryOnFailure;
import com.lee.db_lock.entity.Browse;
import com.lee.db_lock.entity.Catalog;
import com.lee.db_lock.repository.CatalogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class CatalogService {

    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private BrowseService browseService;

    /**
     * 读取浏览次数采用悲观锁，更新浏览次数不加锁，线程安全无需重试
     *
     * @param catalogId 商品id
     * @param user      浏览的用户
     */
    @Transactional(rollbackFor = Exception.class)  //任何异常都回滚(此处目的是使获取乐观锁失败抛出异常的事务回滚),默认只是RuntimeException回滚
    public void browseCatalog(Long catalogId, String user) {
        //1、读取此商品
        //悲观锁读取
        Optional<Catalog>  catalogOptional = catalogRepository.findCatalogForUpdate(catalogId);
//        Optional<Catalog>  catalogOptional = catalogRepository.findCatalogWithPessimisticLock(catalogId);
        if (!catalogOptional.isPresent()) {
            throw new RuntimeException("不存在该商品!");
        }
        Catalog catalog = catalogOptional.get();
        //2、新增浏览记录
        Browse browse = Browse.builder().cataId(catalog.getId()).user(user).build();
        browseService.save(browse);
        //3、不加锁更新浏览次数
        catalog.setBrowseCount(catalog.getBrowseCount() + 1);
        catalogRepository.save(catalog);
    }


    /**
     * 不使用@Transactional(rollbackFor = Exception.class)，而是使用@RetryOnFailure切面对获取乐观锁失败的事务重试
     * 读取浏览次数不加锁，更新浏览次数采用乐观锁，线程安全
     *
     * @param catalogId 商品id
     * @param user      浏览的用户
     */
    @RetryOnFailure  //自定义重试切面注解
    public void browseCatalogWithRetry(Long catalogId, String user) {
        //1、读取此商品
        //不加锁读取
        Optional<Catalog> catalogOptional = catalogRepository.findById(catalogId);
        if (!catalogOptional.isPresent()) {
            log.error("不存在该商品!");
        }
        Catalog catalog = catalogOptional.get();
        //2、新增浏览记录
        Browse browse = Browse.builder().cataId(catalog.getId()).user(user).build();
        browseService.save(browse);  //TODO 获取锁失败的记录不能回滚
        //3、乐观锁更新此商品的浏览次数
        //乐观锁方式1的写法(此方式获取乐观锁失败时不能抛出异常)
//        int result = catalogRepository.updateCatalogWithVersion(catalogId,catalog.getBrowseCount() + 1,catalog.getVersion());
//        if (result == 0) {
//            log.error("server is busy, version={},result={}", catalog.getVersion(), "失败");
//        }else log.info("version={},result={}", catalog.getVersion(), "成功");

        //乐观锁方式2的写法(使用@Version)
        catalog.setBrowseCount(catalog.getBrowseCount() + 1);
        catalogRepository.save(catalog);
    }

}