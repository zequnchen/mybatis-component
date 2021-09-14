package com.mybatis.component.annotation

/**
 * @program: easiclass-minder
 * @description: 字段
 * @author: chenzequn
 * @date: 2021-06-02 16:34
 **/
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(val value: String, val comment: String){

}