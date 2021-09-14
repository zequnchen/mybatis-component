package com.mybatis.component

import com.mybatis.component.codegen.MybatisCodeGen
import java.util.*

/**
 * @program: mybatis-component
 * @description:
 * @author: chenzequn
 * @date: 2021-06-15 21:57
 **/

fun main() {
    var myBatisCodeGen = MybatisCodeGen()

    // 数据库信息
    myBatisCodeGen.dbUrl =""
    myBatisCodeGen.dbName =""
    myBatisCodeGen.dbUserName = ""
    myBatisCodeGen.dbUserPassword = ""
    // 选择生成表的前缀
    myBatisCodeGen.tablePrefix = "t_"
    // 包含指定表 可多个表同时生成
    myBatisCodeGen.tableIncludes = arrayListOf("t_device_app_record")
   // 实体类基础包名
    myBatisCodeGen.packageName ="com.mybatis.component"
//    myBatisCodeGen.subProjectPath="easiminder-server"
    // 类生成的包默认 com.mybatis.component.po com.mybatis.component.dao com.mybatis.component.vo
    // 可自定义配置包位置
//    myBatisCodeGen.entityPackage
//    myBatisCodeGen.mapperPackage
//    myBatisCodeGen.queryWrapperPackage
//    myBatisCodeGen.xmlPackage = ""
    // 是否重写覆盖po类和queryWarpper，其他类只会生成一次，可以删除后重新生成
    myBatisCodeGen.rewriteFile = true
    myBatisCodeGen.generate()
}