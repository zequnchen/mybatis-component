package com.mybatis.component.mapper

import com.mybatis.component.vo.BaseQueryWrapper
import org.apache.ibatis.annotations.*
import java.util.ArrayList


/**
 * @author chenzequn
 */
interface BaseMapper<Po> {

    /**
     * 新增一条记录
     *
     * @param po 实体
     * @return 受影响记录
     */
    @InsertProvider(type = BaseSqlBuilder::class, method = "insert")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty="id")
    fun insert(po: Po) : Int
    /**
     * 新增一条记录
     *
     * @param po 实体
     * @return 受影响记录
     */
    @InsertProvider(type = BaseSqlBuilder::class, method = "insertOrUpdate")
    fun insertOrUpdate(po: Po) : Int
    /**
     * 批量新增记录
     *
     * @param poList 实体
     * @return 受影响记录
     */
    @InsertProvider(type = BaseSqlBuilder::class, method = "batchInsert")
    fun batchInsert(@Param("poList")poList: ArrayList<Po>) : Int
    /**
     * 根据id更新实体字段，字段为Null或者空不会更新字段
     *
     * @param po Po
     * @return 受影响记录
     */
    @UpdateProvider(type = BaseSqlBuilder::class, method = "updateByKey")
    fun updateByKey(po: Po): Int
    /**
     * 根据id更新实体所有字段，为Null值也会更新
     * @param po Po
     * @return 受影响记录
     */
    @UpdateProvider(type = BaseSqlBuilder::class, method = "updateByKeyWithNull")
    fun updateByKeyWithNull(po: Po): Int
    /**
     * 根据id更新实体字段，字段为Null或者空不会更新字段
     *
     * @param po Po
     * @return 受影响记录
     */
    @UpdateProvider(type = BaseSqlBuilder::class, method = "updateByWrapper")
    fun updateByWrapper(@Param("po")po: Po, @Param("query")baseQueryWrapper: BaseQueryWrapper): Int
    /**
     * 根据id更新实体所有字段，为Null值也会更新
     * @param po Po
     * @return 受影响记录
     */
    @UpdateProvider(type = BaseSqlBuilder::class, method = "updateByWrapperWithNull")
    fun updateByWrapperWithNull(@Param("po")po: Po, @Param("query")baseQueryWrapper: BaseQueryWrapper): Int
    /**
     * 删除一条记录
     *
     * @param id id
     * @return 受影响记录
     */
    @UpdateProvider(type = BaseSqlBuilder::class, method = "deleteByKey")
    fun deleteByKey(id: Any): Int
    /**
     * 根据id或者唯一键id查找 优先用uid
     *
     * @param id id
     * @return Entity
     */
    @SelectProvider(type = BaseSqlBuilder::class, method = "selectByKey")
    fun selectByKey(id: Any): Po?
    /**
     * 根据queryWrapper查询
     */
    @SelectProvider(type = BaseSqlBuilder::class, method = "selectByWrapper")
    fun selectByWrapper(@Param("query")queryWrapper: BaseQueryWrapper): List<Po>
    /**
     * 根据queryWrapper查询one
     */
    @SelectProvider(type = BaseSqlBuilder::class, method = "selectOneByWrapper")
    fun selectOneByWrapper(@Param("query")queryWrapper: BaseQueryWrapper): Po?
    /**
     * 根据queryWrapper查询
     */
    @SelectProvider(type = BaseSqlBuilder::class, method = "selectCountByWrapper")
    fun selectCountByWrapper(@Param("query")queryWrapper: BaseQueryWrapper): Long

}