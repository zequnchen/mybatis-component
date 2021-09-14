//
//import com.mybatis.component.Application
//import com.mybatis.component.dao.DeviceStrategyGroupInfoMapper
//import com.mybatis.component.po.DeviceStrategyGroupInfoPo
//import com.mybatis.component.vo.DeviceStrategyGroupInfoQueryWrapper
//import org.apache.ibatis.scripting.xmltags.OgnlCache
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.context.annotation.ComponentScan
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
//import java.util.*
//
//
///**
// * @program: mybatis-component
// * @description:
// * @author: chenzequn
// * @date: 2021-06-16 16:16
// **/
//
//@RunWith(SpringJUnit4ClassRunner::class)
//@SpringBootTest(classes = [Application::class])
//@ComponentScan("com.mybatis.component")
//class Test {
//
//    @Autowired
//    private lateinit var deviceStrategyGroupInfoMapper: DeviceStrategyGroupInfoMapper
//
//    @Test
//    fun testMapper(){
////        val deviceStrategyGroupInfoPo = deviceStrategyGroupInfoMapper.selectByKey("fc3af6b36d4f4ec99287597fa2c31f39")
//
////
//        var poList = ArrayList<DeviceStrategyGroupInfoPo>()
//        poList.add(DeviceStrategyGroupInfoPo().apply {
//            this.uid = "10002"
//            this.isDeleted = 1
//            this.schoolId = "22233"
//            this.createTime = Date()
//            this.strategyName = "测试"
//            this.strategyType = 3
//            this.updateTime= Date()
//        })
////        poList.add(DeviceStrategyGroupInfoPo().apply {
////            this.uid = "10001"
////            this.isDeleted = 1
////            this.schoolId = "222333"
////            this.createTime = Date()
////            this.strategyName = "测试1"
////            this.strategyType = 3
////            this.updateTime= Date()
////        })
////
////        poList.add(DeviceStrategyGroupInfoPo().apply {
////            this.uid = "10000"
////            this.isDeleted = 1
////            this.schoolId = "2223332"
////            this.createTime = Date()
////            this.strategyName = "测试2"
////            this.strategyType = 3
////            this.updateTime= Date()
////        })
//
////        val insert = deviceStrategyGroupInfoMapper.insertOrUpdate(deviceStrategyGroupInfoPo)
////        deviceStrategyGroupInfoPo.isDeleted = 0
////        val selectByWrapper = deviceStrategyGroupInfoMapper.updateByWrapperWithNull(deviceStrategyGroupInfoPo, deviceStrategyGroupInfoQuery)
//        val deviceStrategyGroupInfoQuery = DeviceStrategyGroupInfoQueryWrapper()
//                .andStrategyNameEqules("测试").andIsDeletedEqules(2).or()
//                .andStrategyNameEqules("测试12").andIsDeletedEqules(3)
//
////        val selectByWrapper = deviceStrategyGroupInfoMapper.selectByWrapper( deviceStrategyGroupInfoQuery)
//
//        deviceStrategyGroupInfoMapper.insertOrUpdate(poList[0])
////        val deviceStrategyGroupInfoPo = DeviceStrategyGroupInfoPo().apply {
////            this.isDeleted = 1
////            this.updateTime = Date()
////        }
////
////        val updateByKey = deviceStrategyGroupInfoMapper.updateByWrapperWithNull(deviceStrategyGroupInfoPo, deviceStrategyGroupInfoQuery)
////        val delete = deviceStrategyGroupInfoMapper.deleteByKey("fc3af6b36d4f4ec99287597fa2c31f39")
////        val insert = deviceStrategyGroupInfoMapper.batchInsert(poList)
////        val countByWrapper = deviceStrategyGroupInfoMapper.selectCountByWrapper(deviceStrategyGroupInfoQuery)
////        val insertOrUpdate = deviceStrategyGroupInfoMapper.insertOrUpdate(poList[2])
//        print("完成")
//    }
//
//}