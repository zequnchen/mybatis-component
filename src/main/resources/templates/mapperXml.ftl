<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperPackage}.${entityUpcaseName}Mapper">


 <sql id="pageCondition">
  <#noparse>
  <if test="query.pageable != null and query.pageable.offset >= 0 and query.pageable.pageSize > 0">
   <if test="query.pageable.sort != null and query.pageable.sort.iterator.hasNext">
    ORDER BY
    <foreach collection="query.pageable.sort" item="order"
             index="index" separator=",">
     ${order.property} ${order.direction}
    </foreach>
   </if>
   LIMIT #{query.pageable.offset}, #{query.pageable.pageSize}
  </if>
  </#noparse>
 </sql>

</mapper>