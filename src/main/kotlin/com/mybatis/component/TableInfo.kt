package com.mybatis.component

import java.util.*

/**
 * @program: mybatis-component
 * @description: 表信息
 * @author: chenzequn
 * @date: 2021-06-15 15:46
 **/

class TableInfo (
    var tableName: String? = null,

    var tableComment: String? = null,

    var primaryKey: String? = null,

    var uniqueKey: String? =null,

    var deletedKey: String? =null,

    var columnInfoList: MutableList<ColumnInfo?>? = ArrayList()
)