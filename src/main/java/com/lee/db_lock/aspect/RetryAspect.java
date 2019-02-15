package com.lee.db_lock.aspect;

import com.lee.db_lock.entity.Browse;
import com.lee.db_lock.service.BrowseService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.hibernate.StaleObjectStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * 自定义重试注解 的AOP切面
 */
@Slf4j
@Aspect
@Component
public class RetryAspect {

    @Autowired
    private BrowseService browseService;

    //重试最大次数(根据电脑性能不同调整)
    private static final int MAX_RETRY_TIMES = 25;

    @Pointcut("@annotation(com.lee.db_lock.annotation.RetryOnFailure)")  //切点是自定义重试的注解
    public void retryOnFailure(){}

    /**
     * Spring AOP提供使用org.aspectj.lang.JoinPoint类型获取连接点数据，
     * JoinPoint：提供访问当前被通知方法的目标对象、代理对象、方法参数等数据
     * 任何通知方法的第一个参数都可以是JoinPoint(环绕通知是ProceedingJoinPoint，JoinPoint子类)
     */
    @Around("retryOnFailure()")  //调用retryOnFailure()环绕通知，环绕通知类似于动态代理的全过程,可以决定是否调用目标方法
    public void doConcurrentOperation(ProceedingJoinPoint pjp){
        //初始化重试次数
        int attempts = 0;
        do {
            attempts++;
            //尝试执行目标方法，若获取乐观锁失败会抛出ObjectOptimisticLockingFailureException
            try {
                pjp.proceed();
                log.info("用户{}第{}次retry操作成功...",pjp.getArgs()[1],attempts);
                //新增用户浏览记录
                Browse browse = Browse.builder().cataId((Long) pjp.getArgs()[0]).user((String) pjp.getArgs()[1]).build();
                browseService.save(browse);
                return;   //重试成功,结束
            } catch (Throwable e) {    //Throwable是Exception的父类
                //若不是jpa实现的乐观获取锁失败时抛出的异常,出错结束
                if (!(e instanceof ObjectOptimisticLockingFailureException) && !(e instanceof StaleObjectStateException)){
                    log.error("用户{}第{}次retry发生其他异常，操作失败...",pjp.getArgs()[1],attempts);
                    return;
                }
                //超出最大重试次数，出错结束
                if(attempts >= MAX_RETRY_TIMES) {
                    log.error("用户{}第{}次retry已达到最大重试次数，操作失败...",pjp.getArgs()[1],attempts);
                    return;
                }
                //否则继续重试
            }
        } while (true);
    }

}