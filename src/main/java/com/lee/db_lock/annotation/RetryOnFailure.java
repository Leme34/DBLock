package com.lee.db_lock.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：用于做切面，实现在乐观锁失败后重试
 */
@Target(ElementType.METHOD)  //方法注解
@Retention(RetentionPolicy.RUNTIME)  //注解在运行时有效
public @interface RetryOnFailure {
}
