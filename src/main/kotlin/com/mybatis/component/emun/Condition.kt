package com.mybatis.component.emun

/**
 * @program: mybatis-component
 * @description: 条件枚举
 * @author: chenzequn
 * @date: 2021-07-11 00:59
 **/
enum class Condition(val symbol: String) {
    EQULES("="),
    MORE_THEN(">"),
    MORE_THEN_AND_EQULES(">="),
    LESS_THEN("<"),
    LESS_THEN_AND_EQULES("<="),
    IN("in"),
    LIKE("like"),
    BETWEEN("between"),
    AND("and"),
    OR("or"),
    NULL("is null"),
    NOT_NULL("is not null")
    ;
}