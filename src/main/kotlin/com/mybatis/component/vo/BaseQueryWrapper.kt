package com.mybatis.component.vo

import org.springframework.data.domain.Pageable

/**
 * @program: mybatis-component
 * @description: 基础条件类
 * @author: chenzequn
 * @date: 2021-07-11 00:28
 **/
abstract class BaseQueryWrapper{

    var param = ArrayList<ParamCondition>()

    abstract val selectColumn: BaseSelectColumn

    var pageable: Pageable? = null

    var limit: Int? = null

    var orderBy: String? = null

    open class BaseSelectColumn{
        val selectColumn: MutableList<String> = ArrayList()
    }

    abstract fun getPoClass(): Class<*>

    fun clearColumn(column: String){
        param = param.filter { it.column != column } as ArrayList<ParamCondition>
    }


}