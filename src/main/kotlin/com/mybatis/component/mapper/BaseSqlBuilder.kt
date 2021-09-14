package com.mybatis.component.mapper

import com.mybatis.component.emun.Condition
import com.mybatis.component.emun.LikeType
import com.mybatis.component.meta.TableMetaData
import com.mybatis.component.vo.BaseQueryWrapper
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.builder.annotation.ProviderContext
import org.apache.ibatis.jdbc.SQL
import org.apache.ibatis.scripting.xmltags.ExpressionEvaluator
import org.apache.ibatis.scripting.xmltags.OgnlCache
import org.springframework.core.ResolvableType
import org.springframework.util.Assert

/**
 * @program: easiclass-minder
 * @description: sql生成基础类
 * @author: chenzequn
 * @date: 2021-06-02 15:40
 **/
class BaseSqlBuilder {

    companion object{
        val DEFAULT_CREATE_TIME = "create_time"
    }

    val expressionEvaluator = ExpressionEvaluator()
    /**
     * 根据主键或者uid唯一键查找记录 uid优先
     */
    public fun <T> selectByKey(context: ProviderContext): String? {
        val entityClass = getEntityClass(context)
        if (entityClass != null) {
            val sql = SQL().SELECT("*")
            val tableMetaData = TableMetaData.forClass(entityClass)
            sql.FROM(tableMetaData.tableName)
            val uniqueKeyColumn = tableMetaData.uniqueKeyColumn
            if (uniqueKeyColumn != null) {
                sql.WHERE("${uniqueKeyColumn.column} = #{${uniqueKeyColumn.property}}")
            } else {
                sql.WHERE("${tableMetaData.pkColumn?.column} = #{${tableMetaData.pkColumn?.property}}")
            }
            return sql.toString()
        }
        return null
    }
    /**
     * 根据主键或者uid唯一键查找记录 uid优先
     */
    public fun <T> deleteByKey(context: ProviderContext): String? {
        val entityClass = getEntityClass(context)
        if (entityClass != null) {
            val tableMetaData = TableMetaData.forClass(entityClass)
            val sql = SQL().UPDATE(tableMetaData.tableName)
            //不存在isDeleted 不允许删除
            val deletedColumn = tableMetaData.isDeletedColumn
            if (deletedColumn == null){
                return null
            }
            sql.SET("${deletedColumn.column} = 1")
            val updateTime = tableMetaData.updateTimeColumn
            if (updateTime != null){
                sql.SET("${updateTime.column} = now()")
            }
            val uniqueKeyColumn = tableMetaData.uniqueKeyColumn
            if (uniqueKeyColumn != null) {
                sql.WHERE("${uniqueKeyColumn.column} = #{${uniqueKeyColumn.property}}")
            } else {
                sql.WHERE("${tableMetaData.pkColumn?.column} = #{${tableMetaData.pkColumn?.property}}")
            }
            return sql.toString()
        }
        return null
    }
    /**
     * 根据条件包装器查找
     */
    fun selectByWrapper(@Param("query")baseQueryWrapper: BaseQueryWrapper): String? {
        val entityClass = baseQueryWrapper.getPoClass()
        val sql = SQL().SELECT(selectColumn(baseQueryWrapper))
        val tableMetaData = TableMetaData.forClass(entityClass)
        sql.FROM(tableMetaData.tableName)
        whereSql(baseQueryWrapper, sql)
        pageOrderBy(baseQueryWrapper, sql)
        return sql.toString()
    }
    /**
     * 根据条件包装器查找Count
     */
    fun selectCountByWrapper(@Param("query")baseQueryWrapper: BaseQueryWrapper): String? {
        val entityClass = baseQueryWrapper.getPoClass()
        val sql = SQL().SELECT("count(1)")
        val tableMetaData = TableMetaData.forClass(entityClass)
        sql.FROM(tableMetaData.tableName)
        whereSql(baseQueryWrapper, sql)
        return sql.toString()
    }
    /**
     * 根据条件包装器查找
     */
    fun selectOneByWrapper(@Param("query")baseQueryWrapper: BaseQueryWrapper): String? {
        val entityClass = baseQueryWrapper.getPoClass()
        val sql = SQL().SELECT(selectColumn(baseQueryWrapper))
        val tableMetaData = TableMetaData.forClass(entityClass)
        sql.FROM(tableMetaData.tableName)
        whereSql(baseQueryWrapper, sql)
        baseQueryWrapper.limit = 1
        pageOrderBy(baseQueryWrapper, sql)
        return sql.toString()
    }
    /**
     * 插入记录
     */
    fun <Po: Any> insert(po: Po) : String{
        Assert.notNull(po, "po must not null")
        val entityClass = po::class.java
        val tableMetaData = TableMetaData.forClass(entityClass)
        val fieldList = tableMetaData.fieldList
        val uniqueKeyColumn = tableMetaData.uniqueKeyColumn

        var sql = SQL()
        sql.INSERT_INTO(tableMetaData.tableName)
        if (uniqueKeyColumn != null) {
            sql.VALUES(uniqueKeyColumn.column, "#{${uniqueKeyColumn.property}}")
        }
        fieldList.forEach continuing@{
            val evaluateBoolean = expressionEvaluator.evaluateBoolean("${it.property} != null", po)
            if (evaluateBoolean) {
                sql.VALUES(it.column, "#{${it.property}}")
            }

        }
        return sql.toString()
    }
    /**
     * 插入或更新记录 通过id或uid 优先uid
     */
    fun <Po: Any> insertOrUpdate(po: Po) : String{
        Assert.notNull(po, "po must not null")
        val entityClass = po::class.java
        val tableMetaData = TableMetaData.forClass(entityClass)
        val fieldList = tableMetaData.fieldList
        val sql = StringBuffer()
        sql.append(insert(po))
        sql.append("\nON DUPLICATE KEY UPDATE\n ")
        var i = 0
        fieldList.forEach {
            if (!DEFAULT_CREATE_TIME.equals(it.column)){
                val evaluateBoolean = expressionEvaluator.evaluateBoolean("${it.property} != null", po)
                if (evaluateBoolean) {
                    if (i > 0) {
                        sql.append(",")
                    }
                    sql.append("${it.column} = #{${it.property}}")
                    i++
                }
            }
        }
        return sql.toString()
    }
    /**
     * 插入记录
     */
    fun <Po: Any> batchInsert(@Param("poList")poList: List<Po>) : String{
        Assert.notNull(poList, "po must not null")
        val entityClass = poList.first()::class.java
        val tableMetaData = TableMetaData.forClass(entityClass)
        val fieldList = tableMetaData.fieldList
        val uniqueKeyColumn = tableMetaData.uniqueKeyColumn

        var sql = SQL()
        sql.INSERT_INTO(tableMetaData.tableName)
        var i = 0
        poList.forEach {po ->
            if (uniqueKeyColumn != null) {
                if (i == 0) sql.INTO_COLUMNS(uniqueKeyColumn.column)
                sql.INTO_VALUES("#{poList[${i}].${uniqueKeyColumn.property}}")
            }
            fieldList.forEach continuing@{
                val evaluateBoolean = expressionEvaluator.evaluateBoolean("${it.property} != null", po)
                if (evaluateBoolean) {
                    if (i == 0) sql.INTO_COLUMNS(it.column)
                    sql.INTO_VALUES("#{poList[${i}].${it.property}}")
                }

            }
            sql.ADD_ROW()
            i++
        }

        return sql.toString()
    }
    /**
     * 根据主键或者uid唯一键更新记录 uid优先
     */
    fun <Po : Any> updateByKey(po: Po) : String {
        Assert.notNull(po, "po must not null")
        val entityClass = po::class.java
        val tableMetaData = TableMetaData.forClass(entityClass)
        val fieldList = tableMetaData.fieldList
        val uniqueKeyColumn = tableMetaData.uniqueKeyColumn
        val pkColumn = tableMetaData.pkColumn
        val sql = SQL()
        sql.UPDATE(tableMetaData.tableName)
        var time = System.currentTimeMillis()

        fieldList.forEach continuing@{
            val evaluateBoolean = expressionEvaluator.evaluateBoolean("${it.property} != null", po)
            if (evaluateBoolean) {
                sql.SET("${it.column} = #{${it.property}}")
            }
        }

//        println("ognl获取参数时间${System.currentTimeMillis() - time}ms")

        if (uniqueKeyColumn != null) {
            sql.WHERE("${uniqueKeyColumn.column} = #{${uniqueKeyColumn.property}}")
        } else {
            sql.WHERE("${pkColumn?.column} = #{${pkColumn?.property}}")
        }

        return sql.toString()
    }
    /**
     * 根据主键或者uid唯一键更新记录 uid优先 （null也更新）
     */
    fun <Po : Any> updateByKeyWithNull(po: Po) : String {
        Assert.notNull(po, "po must not null")
        val entityClass = po::class.java
        val tableMetaData = TableMetaData.forClass(entityClass)
        val fieldList = tableMetaData.fieldList
        val uniqueKeyColumn = tableMetaData.uniqueKeyColumn
        val pkColumn = tableMetaData.pkColumn
        val sql = SQL()
        sql.UPDATE(tableMetaData.tableName)

        fieldList.forEach continuing@{
            sql.SET("${it.column} = #{${it.property}}")
        }
        if (uniqueKeyColumn != null) {
            sql.WHERE("${uniqueKeyColumn.column} = #{${uniqueKeyColumn.property}}")
        } else {

            sql.WHERE("${pkColumn?.column} = #{${pkColumn?.property}}")
        }

        return sql.toString()
    }
    /**
     * 根据queryWrapper更新记录 uid优先
     */
    fun <Po : Any> updateByWrapper(@Param("po")po: Po, @Param("query")baseQueryWrapper: BaseQueryWrapper) : String {
        Assert.notNull(po, "po must not null")
        val entityClass = po::class.java
        val tableMetaData = TableMetaData.forClass(entityClass)
        val fieldList = tableMetaData.fieldList
        val sql = SQL()
        sql.UPDATE(tableMetaData.tableName)
        fieldList.forEach continuing@{
            val evaluateBoolean = expressionEvaluator.evaluateBoolean("${it.property} != null", po)
            if (evaluateBoolean) {
                sql.SET("${it.column} = #{po.${it.property}}")
            }
        }
        whereSql(baseQueryWrapper, sql)
        return sql.toString()
    }
    /**
     * 根据queryWrapper更新记录 uid优先 （null也更新)
     */
    fun <Po : Any> updateByWrapperWithNull(@Param("po")po: Po, @Param("query")baseQueryWrapper: BaseQueryWrapper) : String {
        Assert.notNull(po, "po must not null")
        val entityClass = po::class.java
        val tableMetaData = TableMetaData.forClass(entityClass)
        val fieldList = tableMetaData.fieldList
        val sql = SQL()
        sql.UPDATE(tableMetaData.tableName)
        fieldList.forEach continuing@{
            sql.SET("${it.column} = #{po.${it.property}}")
        }
        whereSql(baseQueryWrapper, sql)
        return sql.toString()
    }

    private fun getEntityClass(context: ProviderContext): Class<*>?{
        val mapperType = context.mapperType
        for (parent in mapperType.genericInterfaces) {
            val parentType = ResolvableType.forType(parent)
            if (parentType.rawClass == BaseMapper::class.java) {
                return parentType.getGeneric(0).rawClass
            }
        }
        return null
    }

    private fun selectColumn(baseQueryWrapper: BaseQueryWrapper): String {
        val selectColumn = baseQueryWrapper.selectColumn.selectColumn
        return if (selectColumn.isNullOrEmpty()){
            "*"
        } else {
            selectColumn.joinToString { it }
        }

    }

    private fun whereSql(baseQueryWrapper: BaseQueryWrapper, sql: SQL){
        var i = 0
        baseQueryWrapper.param.forEach continuing@{ paramCondition ->
            when (paramCondition.condition){
                Condition.EQULES,
                Condition.LESS_THEN,
                Condition.LESS_THEN_AND_EQULES,
                Condition.MORE_THEN,
                Condition.MORE_THEN_AND_EQULES
                -> {
                    sql.WHERE("${paramCondition.column} ${paramCondition.condition.symbol}  #{query.param[${i++}].value}" )
                }
                Condition.IN -> {
                    if (paramCondition.value is List<*>){
                        var j = 0
                        val whereValue = paramCondition.value.joinToString {
                            "#{query.param[$i].value[${j++}]}"
                        }
                        i++
                        sql.WHERE("${paramCondition.column} ${paramCondition.condition.symbol}  (${whereValue})" )
                    }
                }
                Condition.BETWEEN -> {
                    if (paramCondition.value is List<*> && paramCondition.value.size == 2){
                        sql.WHERE("${paramCondition.column} ${paramCondition.condition.symbol}  " +
                                " #{query.param[$i].value[0]} and #{query.param[${i++}].value[1]}" )
                    }
                }
                Condition.LIKE -> {
                    when(paramCondition.likeType){
                        LikeType.DEFAULT -> {
                            sql.WHERE("${paramCondition.column} ${paramCondition.condition.symbol} " +
                                    " CONCAT('%', #{query.param[${i++}].value},'%')" )
                        }
                        LikeType.LEFT -> {
                            sql.WHERE("${paramCondition.column} ${paramCondition.condition.symbol} " +
                                    " CONCAT('%', #{query.param[${i++}].value})" )
                        }
                        LikeType.RIGHT -> {
                            sql.WHERE("${paramCondition.column} ${paramCondition.condition.symbol} " +
                                    " CONCAT(#{query.param[${i++}].value}, '%')" )

                        }
                    }
                }
                Condition.OR -> {
                    sql.OR()
                    sql.WHERE()
                    i++
                }
                Condition.AND -> {
                    sql.AND()
                    sql.WHERE()
                    i++
                }

                Condition.NULL,Condition.NOT_NULL -> {
                    sql.WHERE("${paramCondition.column} ${paramCondition.condition.symbol}" )
                    i++
                }
            }
        }
    }

    private fun pageOrderBy(baseQueryWrapper: BaseQueryWrapper, sql: SQL){
        val pageable = baseQueryWrapper.pageable
        if (pageable != null) {
            sql.LIMIT(pageable.pageSize).OFFSET(pageable.offset.toLong())
            if (pageable.sort != null) {
                pageable.sort.forEach{
                    sql.ORDER_BY("${it.property} ${it.direction}")
                }
            }
        } else {
            //不基于分页做 排序+limit
            if (baseQueryWrapper.orderBy != null) {
                sql.ORDER_BY(baseQueryWrapper.orderBy )
            }
            val limit = baseQueryWrapper.limit
            if (limit != null) {
                sql.LIMIT(limit)
            }
        }

    }



}
