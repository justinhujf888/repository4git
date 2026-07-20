package com.weavict.mqttspserver

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Component


@Configuration
class RedisConfig(
    private val redisConnectionFactory: RedisConnectionFactory
) {

    @Bean
    fun functionDomainRedisTemplate(): RedisTemplate<String, Any> {
        return createRedisTemplate()
    }

    private fun <T : Any> createRedisTemplate(): RedisTemplate<String, T> {
        return RedisTemplate<String, T>().apply {
            keySerializer = StringRedisSerializer()
            hashKeySerializer = StringRedisSerializer()
            hashValueSerializer = JdkSerializationRedisSerializer()
            valueSerializer = JdkSerializationRedisSerializer()
            setConnectionFactory(redisConnectionFactory)
        }
    }
}

@Component
class LettuceConnectionValidConfig(
    private val redisConnectionFactory: RedisConnectionFactory
) : InitializingBean {

    override fun afterPropertiesSet() {
        (redisConnectionFactory as? LettuceConnectionFactory)
            ?.setValidateConnection(true)
    }
}