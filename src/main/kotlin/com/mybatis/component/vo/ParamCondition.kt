package com.mybatis.component.vo

import com.mybatis.component.emun.Condition
import com.mybatis.component.emun.LikeType

/**
 * @program: mybatis-component
 * @description: 参数条件
 * @author: chenzequn
 * @date: 2021-07-11 00:57
 **/

class ParamCondition (

        val condition: Condition,
        val value: Any,
        var column: String,
        var likeType: LikeType = LikeType.DEFAULT
)