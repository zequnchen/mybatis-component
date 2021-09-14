package com.mybatis.component.annotation

import java.lang.annotation.ElementType
import java.lang.annotation.RetentionPolicy

/**
 * @program: easiclass-minder
 * @description: 表名注解
 * @author: chenzequn
 * @date: 2021-06-02 16:28
 **/
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Table(val name: String){
}