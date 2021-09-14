package com.mybatis.component.codegen

import com.mybatis.component.ColumnInfo
import com.mybatis.component.TableInfo
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*
import kotlin.collections.ArrayList


/**
 * @program: mybatis-component
 * @description: mybatis生成基类
 * @author: chenzequn
 * @date: 2021-06-09 15:27
 **/

class MybatisCodeGen {

    var dbUrl: String? = null

    var dbName: String? = null

    var dbUserName: String? = null

    var dbUserPassword: String? = null

    var tableComment:String? = null

    /**
     * 生成表信息
     */
    var tablePrefix: String? = null

    var tableIncludes: MutableList<String> = ArrayList()

    var tableExcludes: MutableList<String> = ArrayList()

    /**
     * 生成条件
     */

    var packageName: String? = null

    var entityPackage: String? = null

    var mapperPackage: String?  = null

    var xmlPackage: String  = "mapper"

    var subProjectPath: String? = null

//    var servicePackage: String?  = null
//
//    var serviceImplPackage: String?  = null

    var queryWrapperPackage: String?  = null

    var rewriteFile = false

    var driverClass = "com.mysql.jdbc.Driver"

    companion object{
        //默认controller包名
        val DEFAULT_CONTROLLER_PACKAGE_FORMAT = "com.seewo.eclass.controller"

        val DEFAULT_UNIQUE_KEY = "uid"

        //默认代码包名
        val DEFAULT_PACKAGE_FORMAT = "com.seewo.eclass"

        var projectPath: String? = null
        var codeGenPath: String? = null
        var mapperPath: String? = null

    }



    fun generate() {
        if (dbUrl == null || dbName == null || dbUserName == null || dbUserPassword == null) {
            println("dbUrl dbName dbUserName dbUserPassword必须设置")
            System.exit(1)
        }
        if (tablePrefix == null) {
            println("tablePrefix必须设置")
            System.exit(1)
        }
        if ( packageName == null) {
            println("codeGenPath packageName必须设置")
            System.exit(1)
        }

        //确定项目路径
        projectPath = File("").canonicalPath
        if (subProjectPath != null) {
            projectPath = projectPath.plus("$subProjectPath")
        }
        codeGenPath = "$projectPath/src/main/kotlin/"
        mapperPath = "$projectPath/src/main/resources"

        // 连接数据库查询表信息
        var con: Connection? = null
        try {
            var tableInfoMap: MutableMap<String, TableInfo>  = HashMap<String, TableInfo>()

            Class.forName(driverClass)
            con = DriverManager.getConnection(dbUrl, dbUserName, dbUserPassword)
            var statement: Statement = con.createStatement()
            var resultSet: ResultSet = statement.executeQuery("select * from information_schema.columns where table_schema = '" + dbName + "'")
            while (resultSet.next()) {
                var tableName = resultSet.getString("TABLE_NAME")

                // 检查当前表是否应该处理
                if (!tableName.startsWith(tablePrefix.toString())) {
                    continue
                }
                if (tableIncludes.size > 0 && !tableIncludes.contains(tableName)) {
                    System.out.println("不满足条件的数据表：" + tableName)
                    continue
                }
                if (tableExcludes.contains(tableName)) {
                    System.out.println("不满足条件的数据表：" + tableName)
                    continue
                }

                // 保存表结构
                var tableInfo: TableInfo? = tableInfoMap.get(tableName)
                if (tableInfo == null) {
                    tableInfo = TableInfo()
                    tableInfo.tableName = tableName
                    tableInfoMap.put(tableName, tableInfo)
                }

                var columnInfo = ColumnInfo(
                        resultSet.getString("TABLE_SCHEMA"),
                        resultSet.getString("TABLE_NAME"),
                        resultSet.getString("COLUMN_NAME"),
                        resultSet.getString("COLUMN_TYPE"),
                        resultSet.getString("COLUMN_KEY"),
                        resultSet.getString("COLUMN_COMMENT")
                )

                // 确认主键
                if ("PRI".equals((resultSet.getString("COLUMN_KEY")), true)) {
                    tableInfo.primaryKey = resultSet.getString("COLUMN_NAME")
                }
                // 唯一主键
                if (DEFAULT_UNIQUE_KEY.equals((resultSet.getString("COLUMN_NAME")), true)) {
                    tableInfo.uniqueKey = resultSet.getString("COLUMN_NAME")
                }

                tableInfo.columnInfoList?.add(columnInfo)
            }

            // 生成代码
            var tableSet: Set<String>  = tableInfoMap.keys
            tableSet.forEach {
                tableInfoToCode(tableInfoMap.get(it))
            }

        } catch (e:Exception) {
            e.printStackTrace()
        } finally {
            if (con != null) {
                try {
                    con.close()
                }catch (e: Exception) {}
            }
        }
    }

    fun tableInfoToCode(tableInfo: TableInfo?) {
        try {

            var tableName: String? = tableInfo?.tableName
            var entityName: String = formatName(tableName, false, true) + "Po"
            var entityUpcaseName: String = formatName(tableName, true, true)
            System.out.println("tableName= $tableName, entityName= $entityName, entityUpcaseName=$entityUpcaseName")
            var dataMap: MutableMap<String, Any?> = HashMap()
            dataMap.put("tableName", tableName)
            dataMap.put("tableComment", this.getCommentByTableName(tableName))
            dataMap.put("primaryColumn", tableInfo?.primaryKey)
            dataMap.put("uniqueColumn", tableInfo?.uniqueKey ?: "")
            dataMap.put("primaryProperty", "#{" + formatName(tableInfo?.primaryKey, false, true) + "}")
            dataMap.put("Column", tableInfo?.primaryKey)
            dataMap.put("primaryProperty", "#{" + formatName(tableInfo?.primaryKey, false, true) + "}")
            dataMap.put("entityName", entityName)
            dataMap.put("entityUpcaseName", entityUpcaseName)
            dataMap.put("packageName", packageName)

            entityPackage = entityPackage ?: "$packageName.po"
            mapperPackage = mapperPackage ?: "$packageName.dao"
//            servicePackage = servicePackage ?: "$packageName.service"
//            serviceImplPackage = serviceImplPackage ?: "$serviceImplPackage.service.impl"
            queryWrapperPackage = queryWrapperPackage ?: "$packageName.vo"


            dataMap.put("entityPackage", entityPackage)
            dataMap.put("mapperPackage", mapperPackage)
//            dataMap.put("servicePackage", servicePackage)
//            dataMap.put("serviceImplPackage", serviceImplPackage)
            dataMap.put("queryWrapperPackage", queryWrapperPackage)

            var columnInfoList: MutableList<ColumnInfo?>? = tableInfo?.columnInfoList
            var propertyList: MutableList<Map<String, Any?>> = columnToProperty(columnInfoList)

            dataMap.put("propertyList", propertyList)


            // 生成实体类
            File( "$codeGenPath/${entityPackage!!.replace(".", "/")}").mkdirs()
            var entityFile = File( "$codeGenPath/${entityPackage!!.replace(".", "/")}/${entityUpcaseName}Po.kt")
            if (!entityFile.exists() || rewriteFile) {
                FreeMarkerTemplateUtil.getTemplate("entity.ftl")?.process(dataMap, BufferedWriter(FileWriter(entityFile)))
            }
            // 生成实体类
            File( "$codeGenPath/${queryWrapperPackage!!.replace(".", "/")}").mkdirs()
            var queryWrapperFile = File( "$codeGenPath/${queryWrapperPackage!!.replace(".", "/")}/${entityUpcaseName}QueryWrapper.kt")
            if (!queryWrapperFile.exists() || rewriteFile) {
                FreeMarkerTemplateUtil.getTemplate("queryWrapper.ftl")?.process(dataMap, BufferedWriter(FileWriter(queryWrapperFile)))
            }
            // 生成Mapper类
            File( "$codeGenPath/${mapperPackage!!.replace(".", "/")}").mkdirs()
            var mapperFile = File(  "$codeGenPath/${mapperPackage!!.replace(".", "/")}/${entityUpcaseName}Mapper.kt")

            if (!mapperFile.exists()) {
                FreeMarkerTemplateUtil.getTemplate("mapper.ftl")?.process(dataMap, BufferedWriter(FileWriter(mapperFile)))
            }
            // 生成Mapper xml
            mapperPath = mapperPath.plus("/$xmlPackage")
            File( mapperPath).mkdirs()
            var mapperXmlFile = File(  "$mapperPath/${entityUpcaseName}Mapper.xml")

            if (!mapperXmlFile.exists()) {
                FreeMarkerTemplateUtil.getTemplate("mapperXml.ftl")?.process(dataMap, BufferedWriter(FileWriter(mapperXmlFile)))
            }
//
//            // 生成Service类
//            File( "$codeGenPath/${servicePackage.replace(".", "/")}").mkdirs()
//            var serviceFile: File = File( "$codeGenPath/${servicePackage.replace(".", "/")}/${entityUpcaseName}Service.kt")
//            if (!serviceFile.exists()) {
//                var bufferedWriter: BufferedWriter = BufferedWriter(FileWriter(serviceFile))
//                FreeMarkerTemplateUtil.getTemplate("service.ftl")?.process(dataMap, bufferedWriter)
//            }
//            // 生成ServiceImpl类
//            File( "$codeGenPath/${serviceImplPackage.replace(".", "/")}").mkdirs()
//            var serviceImplFile: File = File( "$codeGenPath/${serviceImplPackage.replace(".", "/")}/${entityUpcaseName}ServiceImpl.kt")
//            if (!serviceImplFile.exists()) {
//                var bufferedWriter: BufferedWriter = BufferedWriter(FileWriter(serviceImplFile))
//                FreeMarkerTemplateUtil.getTemplate("serviceImpl.ftl")?.process(dataMap, bufferedWriter)
//            }

        }catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * 格式化类名
     * @param tableName
     * @return
     */
    fun formatName(tableName: String?, firstUpcase: Boolean, replacePrefix: Boolean): String {
        var name = tableName
        if (replacePrefix) {
            name = name?.replaceFirst(tablePrefix!!.toRegex(), "")
        }
        val stringTokenizer = StringTokenizer(name, "_", false)
        val stringBuilder = java.lang.StringBuilder()
        var cnt = 0
        while (stringTokenizer.hasMoreElements()) {
            val temp = stringTokenizer.nextToken()
            if (cnt == 0 && !firstUpcase) {
                stringBuilder.append(temp.substring(0, 1).toLowerCase()).append(temp.substring(1))
            } else {
                stringBuilder.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1))
            }
            cnt++
        }
        return stringBuilder.toString()
    }


    @Throws(Exception::class)
    fun getCommentByTableName(table: String?): String? {
        var comment = ""
        val conn = DriverManager.getConnection(dbUrl, dbUserName, dbUserPassword)
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery("SHOW CREATE TABLE $table")
        if (rs != null && rs.next()) {
            val createDDL: String = rs.getString(2)
            comment = parse(createDDL)
        }
        rs!!.close()
        stmt.close()
        conn.close()
        return comment
    }

    /**
     * 返回注释信息
     * @param all
     * @return
     */
    fun parse(all: String): String {
        val index = all.indexOf("COMMENT='")
        if (index < 0) {
            return ""
        }
        var comment = all.substring(index + 9)
        comment = comment.substring(0, comment.length - 1)
        return comment
    }

    /**
     * 列名转属性名
     * @param columnInfoList
     * @return
     */
    private fun columnToProperty(columnInfoList: MutableList<ColumnInfo?>?): MutableList<Map<String, Any?>> {
        val properityList: MutableList<Map<String, Any?>> = ArrayList()
        if (columnInfoList != null) {
            columnInfoList.forEach { columnInfo ->
                val map: MutableMap<String, Any?> = HashMap()
                map["columnName"] = columnInfo?.columnName
                map["columnComment"] = columnInfo?.columnComment
                map["propertyName"] = formatName(columnInfo?.columnName, false, false)
                map["propertyUpcaseName"] = formatName(columnInfo?.columnName, true, false)
                val columnType: String? = columnInfo?.columnType
                var propertyType: String? = null
                if (columnType?.indexOf("bigint") != -1) {
                    propertyType = "BigInteger"
                } else if (columnType.indexOf("smallint") != -1 || columnType.indexOf("mediumint") != -1 || columnType.indexOf("int") != -1 || columnType.indexOf("tinyint") != -1) {
                    propertyType = "Int"
                } else if (columnType.indexOf("double") != -1) {
                    propertyType = "Double"
                } else if (columnType.indexOf("char") != -1 || columnType.indexOf("varchar") != -1 || columnType.indexOf("text") != -1|| columnType.indexOf("json") != -1) {
                    propertyType = "String"
                } else if (columnType.indexOf("datetime") != -1 || columnType.indexOf("date") != -1 || columnType.indexOf("timestamp") != -1) {
                    propertyType = "Date"
                } else if (columnType.indexOf("time") != -1) {
                    propertyType = "Time"
                } else if (columnType.indexOf("decimal") != -1) {
                    propertyType = "BigDecimal"
                }
                map["propertyType"] = propertyType
                properityList.add(map)
            }
        }
        return properityList
    }
}