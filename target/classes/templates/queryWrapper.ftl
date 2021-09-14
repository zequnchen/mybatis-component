package ${queryWrapperPackage};


import com.mybatis.component.emun.Condition
import com.mybatis.component.emun.LikeType
import ${entityPackage}.${entityUpcaseName}Po
import java.util.*
import com.mybatis.component.vo.*
import java.math.BigInteger
/**
  * ${tableComment} queryWrapper
  */
class ${entityUpcaseName}QueryWrapper: BaseQueryWrapper(){

    override val selectColumn = SelectColumn()

    override fun getPoClass(): Class<*> {
        return ${entityUpcaseName}Po::class.java
    }

    fun and(): ${entityUpcaseName}QueryWrapper {
        param.add(ParamCondition(Condition.AND, "",""))
        return this
    }

    fun or(): ${entityUpcaseName}QueryWrapper {
        param.add(ParamCondition(Condition.OR,"", ""))
        return this
    }

    <#list propertyList! as property>
    <#if primaryColumn != property.columnName && uniqueColumn != property.columnName>
    //${property.propertyName!}
    fun and${property.propertyName?cap_first}Equles(${property.propertyName!}: ${property.propertyType!}): ${entityUpcaseName}QueryWrapper {
        super.param.add(ParamCondition(Condition.EQULES, ${property.propertyName!}, "${property.columnName!}"))
        return this
    }
    fun and${property.propertyName?cap_first}In(${property.propertyName!}s: List<${property.propertyType!}>): ${entityUpcaseName}QueryWrapper {
        super.param.add(ParamCondition(Condition.IN, ${property.propertyName!}s, "${property.columnName!}"))
        return this
    }
    fun and${property.propertyName?cap_first}Like(${property.propertyName!}: ${property.propertyType!}, likeType: LikeType): ${entityUpcaseName}QueryWrapper {
        super.param.add( ParamCondition(Condition.LIKE, ${property.propertyName!}, "${property.columnName!}", likeType))
        return this
    }
    fun and${property.propertyName?cap_first}Like(${property.propertyName!}: ${property.propertyType!}): ${entityUpcaseName}QueryWrapper {
        super.param.add(ParamCondition(Condition.LIKE, ${property.propertyName!}, "${property.columnName!}"))
        return this
    }
    fun and${property.propertyName?cap_first}Between(${property.propertyName!}Start: ${property.propertyType!}, ${property.propertyName!}End: ${property.propertyType!}): ${entityUpcaseName}QueryWrapper {
        super.param.add(ParamCondition(Condition.BETWEEN, arrayListOf(${property.propertyName!}Start, ${property.propertyName!}End) ,"${property.columnName!}"))
        return this
    }
    fun and${property.propertyName?cap_first}MoreThen(${property.propertyName!}: ${property.propertyType!}): ${entityUpcaseName}QueryWrapper  {
        super.param.add(ParamCondition(Condition.MORE_THEN, ${property.propertyName!}, "${property.columnName!}"))
        return this
    }
    fun and${property.propertyName?cap_first}MoreThenAndEqules(${property.propertyName!}: ${property.propertyType!}): ${entityUpcaseName}QueryWrapper  {
        super.param.add(ParamCondition(Condition.MORE_THEN_AND_EQULES, ${property.propertyName!}, "${property.columnName!}"))
        return this
    }
    fun and${property.propertyName?cap_first}LessThen(${property.propertyName!}: ${property.propertyType!}): ${entityUpcaseName}QueryWrapper {
        super.param.add(ParamCondition(Condition.LESS_THEN, ${property.propertyName!}, "${property.columnName!}"))
        return this
    }
    fun and${property.propertyName?cap_first}LessThenAndEqules(${property.propertyName!}: ${property.propertyType!}): ${entityUpcaseName}QueryWrapper {
        super.param.add(ParamCondition(Condition.LESS_THEN_AND_EQULES, ${property.propertyName!}, "${property.columnName!}"))
        return this
    }
    fun clear${property.propertyName?cap_first}(): ${entityUpcaseName}QueryWrapper {
        super.clearColumn("${property.columnName!}")
        return this
    }

    </#if>
    </#list>

    class SelectColumn: BaseSelectColumn() {
    <#list propertyList! as property>
        fun add${property.propertyName?cap_first}(): SelectColumn {
            super.selectColumn.add("${property.columnName!}")
            return this
        }
    </#list>
    }
}
