package com.weavict.shop.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.weavict.shop.entity.Buyer
import com.weavict.shop.entity.Orgration
import org.springframework.stereotype.Service

//import com.weavict.website.common.OtherUtils
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig
//import redis.clients.jedis.Jedis
//import redis.clients.jedis.JedisPool

/**
 * Created by Justin on 2018/6/10.
 */
//@Service("redisService")
class RedisApi
{
//    private static JedisPool pool = null;
//
//    /**
//     17      * 构建redis连接池
//     18      *
//     19      * @param ip
//     20      * @param port
//     21      * @return JedisPool
//     22      */
//    static JedisPool getPool()
//    {
//        if (pool == null)
//        {
//            GenericObjectPoolConfig config = new GenericObjectPoolConfig();
//            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
//            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
//            config.setMaxTotal(OtherUtils.givePropsValue("redis_maxTotal"));
//            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
//            config.setMaxIdle(OtherUtils.givePropsValue("redis_maxIdle"));
//            config.setMinIdle(OtherUtils.givePropsValue("redis_minIdle"));
//            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
//            config.setMaxWaitMillis(OtherUtils.givePropsValue("redis_maxWaitMillis"));
//            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
//            config.setTestOnBorrow(OtherUtils.givePropsValue("redis_testOnBorrow"));
//            pool = new JedisPool(config, OtherUtils.givePropsValue("redis_host"), OtherUtils.givePropsValue("redis_port"));
//        }
//        return pool;
//    }
//
//    /**
//     * 返还到连接池
//     *
//     * @param pool
//     * @param redis
//     */
//    static void returnResource(JedisPool pool, Jedis redis)
//    {
//        if (redis != null)
//        {
//            redis.close();
//        }
//    }
//
//    /**
//     * 获取数据
//     *
//     * @param key
//     * @return
//     */
//    static String get(String key)
//    {
//        String value = null;
//
//        JedisPool pool = null;
//        Jedis jedis = null;
//        try
//        {
//            pool = getPool();
//            jedis = pool.getResource();
//            value = jedis.get(key);
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        } finally
//        {
//            //返还到连接池
//            returnResource(pool, jedis);
//        }
//        return value;
//    }
    String genRedisBuyer(RedisUtil redisUtil,ObjectMapper objectMapper,String buyerId)
    {
        String jsonStr = redisUtil.hGet("buyer_${buyerId}","bean");
        Buyer buyer = null;
        if (!(jsonStr in [null,""]))
        {
            buyer = objectMapper.readValue(jsonStr,Buyer.class);
        }
        else
        {
            buyer = userBean.findObjectById(Buyer.class,buyerId);
        }
        if (buyer.orgrationList==null)
        {
            buyer.orgrationList = new ArrayList<Orgration>();
        }
        buyer.orgrationList.add(orgration);
        redisUtil.hPut("buyer_${buyerId}","bean",objectMapper.writeValueAsString(
                ({
                    return buyer;
                }).call()
        ));
    }
}
