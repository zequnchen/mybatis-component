package ${entityPackage}

import java.math.BigDecimal
import java.util.Date
import java.sql.Time
import com.mybatis.component.annotation.Id
import com.mybatis.component.annotation.Column
import com.mybatis.component.annotation.Table
import com.mybatis.component.annotation.UniqueKey
import java.math.BigInteger

/**
  * ${tableComment}
  */
@Table("${tableName}")
class ${entityUpcaseName}Po(

    <#list propertyList! as property>
    <#if primaryColumn == property.columnName>
    @Id
    </#if>
    <#if uniqueColumn == property.columnName>
    @UniqueKey
    </#if>
    @Column("${property.columnName!}", "${property.columnComment!}")
    var ${property.propertyName!}: ${property.propertyType!}? = null<#if property_index != propertyList?size-1>,

    </#if>
    </#list>

)
