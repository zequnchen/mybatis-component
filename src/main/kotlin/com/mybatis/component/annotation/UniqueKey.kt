package com.mybatis.component.annotation

/**
 * @program: easiclass-minder
 * @description: 唯一键标识 更新根据什么更新 最好主键
 * @author: chenzequn
 * @date: 2021-06-02 16:34
 **/
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class UniqueKey() {

}