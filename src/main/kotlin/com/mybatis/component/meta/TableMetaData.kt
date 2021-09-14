package com.mybatis.component.meta

import com.mybatis.component.annotation.Column
import com.mybatis.component.annotation.Id
import com.mybatis.component.annotation.Table
import com.mybatis.component.annotation.UniqueKey
import java.util.concurrent.ConcurrentHashMap

/**
 * @program: easiclass-minder
 * @description: 数据库元数据
 * @author: chenzequn
 * @date: 2021-06-02 16:14
 **/
class TableMetaData {

    companion object {
        val DEFAULT_DELETE_KEY = "isDeleted"
        val DEFAULT_UPDATE_TIME_KEY = "updateTime"
        val tableCache = ConcurrentHashMap<Class<*>,TableMetaData>(64);
        fun forClass(clazz: Class<*>): TableMetaData{
            var tableMetaData = tableCache.get(clazz)
            if (tableMetaData == null) {
                tableMetaData = TableMetaData(clazz)
                tableCache.put(clazz, tableMetaData)
            }
            return tableMetaData
        }
    }

    var tableName: String? = null


    /**
     * 主键对应的列名
     */
    var pkColumn: ColumMeta? = null

    /**
     * 唯一键对应的列名
     */
    var uniqueKeyColumn: ColumMeta? = null
    /**
     * 逻辑删除列名
     */
    var isDeletedColumn: ColumMeta? = null
    /**
     * 更新时间列名
     */
    var updateTimeColumn: ColumMeta? = null

    /**
     * 属性名和字段名
     */
    var fieldList: MutableList<ColumMeta> = ArrayList()

    private constructor(clazz: Class<*>){
        initTableInfo(clazz);
    }

    private fun initTableInfo(clazz: Class<*>) {
        if (!clazz.isAnnotationPresent(Table::class.java)) return
        tableName = clazz.getAnnotation(Table::class.java).name
        for (field in clazz.declaredFields){
            val id = field.getDeclaredAnnotation(Id::class.java)
            val uniqueKey = field.getDeclaredAnnotation(UniqueKey::class.java)
            val column = field.getDeclaredAnnotation(Column::class.java)
            //防止多出现参数$jacocoData
            if (field.isSynthetic || column == null) {
                continue
            }
            if (DEFAULT_DELETE_KEY == field.name){
                isDeletedColumn = ColumMeta(field.name, column.value)
            }
            if (DEFAULT_UPDATE_TIME_KEY == field.name){
                updateTimeColumn = ColumMeta(field.name, column.value)
            }
            if (id != null) {
                pkColumn = ColumMeta(field.name, column.value)
            } else if (uniqueKey != null) {
                uniqueKeyColumn = ColumMeta(field.name, column.value)
            } else {
                fieldList.add(ColumMeta(field.name, column.value))
            }
        }
    }


}