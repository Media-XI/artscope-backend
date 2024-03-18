package com.example.codebase.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
* <p>분산 락을 사용하기 위한 어노테이션</p>
* key : Lock 을 구분하기 위한 key <br>
* waitTime : 락을 얻기 위해 대기하는 시간 <br>
* leaseTime : 락을 얻은 후 유지하는 시간 <br>
* timeUnit : 시간 단위 <br>
* transactional : 트랜잭션을 사용할지 여부 <br>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {
    String key() default "";
    long waitTime() default 5;
    long leaseTime() default 2;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    boolean transactional() default true;
}
