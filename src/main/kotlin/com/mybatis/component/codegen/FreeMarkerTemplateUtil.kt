package com.mybatis.component.codegen

import freemarker.cache.ClassTemplateLoader
import freemarker.cache.NullCacheStorage
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler

/**
 * @program: mybatis-component
 * @description: 模板生成工具类
 * @author: chenzequn
 * @date: 2021-06-15 17:04
 **/

class FreeMarkerTemplateUtil {


    companion object
    {
        val configuration: Configuration = Configuration(Configuration.VERSION_2_3_22)

        init{
            //这里比较重要，用来指定加载模板所在的路径
            configuration.setTemplateLoader(ClassTemplateLoader(FreeMarkerTemplateUtil::class.java,"/templates"))
            configuration.setDefaultEncoding("UTF-8")
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)
            configuration.setCacheStorage(NullCacheStorage.INSTANCE)
        }
        fun getTemplate(templateName: String?): Template? {
            try {
                return configuration.getTemplate(templateName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

    }



}