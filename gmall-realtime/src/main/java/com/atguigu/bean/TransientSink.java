package com.atguigu.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TransientSink
 *
 * @author Star Zhang
 * @date 2022/4/22 14:39
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransientSink {

//注解的注解叫元注解
    //注解的数据叫元素据
}
