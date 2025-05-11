package com.weavict.carshopapp.app

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
class RedisConfig
{
    @Autowired
    RedisConnectionFactory redisConnectionFactory;

    @Bean
    RedisTemplate<String,Object> functionDomainRedisTemplate()
    {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        initDomainRedisTemplate(redisTemplate, redisConnectionFactory);
        return redisTemplate;
    }

    /*
	*设置redisTemplate序列化策略，否则在使用redisTemplate的时候在redis的客户端查看会出现乱码
	*/
    private void initDomainRedisTemplate(RedisTemplate<String, Object> redisTemplate, RedisConnectionFactory factory)
    {
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setConnectionFactory(factory);
    }
}

@Component
class LettuceConnectionValidConfig implements InitializingBean {
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    void afterPropertiesSet() throws Exception {
        if(redisConnectionFactory instanceof LettuceConnectionFactory){
            LettuceConnectionFactory c=(LettuceConnectionFactory)redisConnectionFactory;
            c.setValidateConnection(true);
        }
    }
}
