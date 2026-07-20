package com.weavict.mqttspserver.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.BeanUtils
import java.util.*

abstract class BaseRest {

    companion object {
        private var objectMapper: ObjectMapper? = null
    }

    protected fun buildObjectMapper(): ObjectMapper {
        return objectMapper ?: ObjectMapper().also { objectMapper = it }
    }

    protected fun processException(e: Exception) {
        e.printStackTrace()
    }

    protected fun beanToMap(bean: Any?): Map<String, Any?> {
        if (bean == null) return emptyMap()

        val map = mutableMapOf<String, Any?>()
        try {
            val descriptors = BeanUtils.getPropertyDescriptors(bean::class.java)
            descriptors.forEach { desc ->
                val key = desc.name
                // 排除 class 和其他特殊属性
                if (key !in setOf("class")) {
                    desc.readMethod?.let { readMethod ->
                        try {
                            map[key] = readMethod.invoke(bean)
                        } catch (e: Exception) {
                            // 属性读取失败，跳过
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // 整体异常处理，避免影响业务
            e.printStackTrace()
        }
        return map
    }

    protected fun <T> mapToBean(map: Map<String, Any?>, bean: T): T {
        if (bean != null) {
            BeanUtils.copyProperties(map, bean)
        }
        return bean
    }

    protected fun objectsToMaps(objList: List<Any>?): List<Map<String, Any?>> {
        if (objList.isNullOrEmpty()) {
            return emptyList()
        }
        return objList.map { beanToMap(it) }
    }

    @Throws(InstantiationException::class, IllegalAccessException::class)
    protected fun <T> mapsToObjects(
        maps: List<Map<String, Any?>>?,
        clazz: Class<T>
    ): List<T> {
        if (maps.isNullOrEmpty()) {
            return emptyList()
        }
        return maps.mapNotNull { map ->
            try {
                val bean = clazz.getDeclaredConstructor().newInstance()
                mapToBean(map, bean)
                bean
            } catch (e: Exception) {
                null
            }
        }
    }

    protected fun <T> objToBean(
        o: Any,
        clazz: Class<T>,
        mapper: ObjectMapper? = null
    ): T {
        val actualMapper = mapper ?: buildObjectMapper()
        return actualMapper.convertValue(o, clazz)
    }
}