package com.weavict.mqttspserver.redis

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.redis.connection.DataType
import org.springframework.data.redis.core.Cursor
import org.springframework.data.redis.core.ScanOptions
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.TimeUnit

@Service
class RedisUtil {

    @Autowired
    private lateinit var redisTemplate: StringRedisTemplate

    fun setRedisTemplate(redisTemplate: StringRedisTemplate) {
        this.redisTemplate = redisTemplate
    }

    fun getRedisTemplate(): StringRedisTemplate = redisTemplate

    // ==================== key 相关操作 ====================

    fun delete(key: String) {
        redisTemplate.delete(key)
    }

    fun delete(keys: Collection<String>) {
        redisTemplate.delete(keys)
    }

    fun dump(key: String): ByteArray = redisTemplate.dump(key)

    fun hasKey(key: String): Boolean = redisTemplate.hasKey(key)

    fun expire(key: String, timeout: Long, unit: TimeUnit): Boolean =
        redisTemplate.expire(key, timeout, unit)

    fun expireAt(key: String, date: Date): Boolean =
        redisTemplate.expireAt(key, date)

    fun keys(pattern: String): Set<String> =
        redisTemplate.keys(pattern) ?: emptySet()

    fun move(key: String, dbIndex: Int): Boolean =
        redisTemplate.move(key, dbIndex)

    fun persist(key: String): Boolean =
        redisTemplate.persist(key)

    fun getExpire(key: String, unit: TimeUnit): Long =
        redisTemplate.getExpire(key, unit)

    fun getExpire(key: String): Long =
        redisTemplate.getExpire(key)

    fun randomKey(): String? =
        redisTemplate.randomKey()

    fun rename(oldKey: String, newKey: String) {
        redisTemplate.rename(oldKey, newKey)
    }

    fun renameIfAbsent(oldKey: String, newKey: String): Boolean =
        redisTemplate.renameIfAbsent(oldKey, newKey)

    fun type(key: String): DataType =
        redisTemplate.type(key)

    // ==================== String 相关操作 ====================

    fun set(key: String, value: String) {
        redisTemplate.opsForValue().set(key, value)
    }

    fun get(key: String): String? =
        redisTemplate.opsForValue().get(key)

    fun getRange(key: String, start: Long, end: Long): String? =
        redisTemplate.opsForValue().get(key, start, end)

    fun getAndSet(key: String, value: String): String? =
        redisTemplate.opsForValue().getAndSet(key, value)

    fun getBit(key: String, offset: Long): Boolean =
        redisTemplate.opsForValue().getBit(key, offset)

    fun multiGet(keys: Collection<String>): List<String>? =
        redisTemplate.opsForValue().multiGet(keys) as List<String>?

    fun setBit(key: String, offset: Long, value: Boolean): Boolean =
        redisTemplate.opsForValue().setBit(key, offset, value)

    fun setEx(key: String, value: String, timeout: Long, unit: TimeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit)
    }

    fun setIfAbsent(key: String, value: String): Boolean =
        redisTemplate.opsForValue().setIfAbsent(key, value)

    fun setRange(key: String, value: String, offset: Long) {
        redisTemplate.opsForValue().set(key, value, offset)
    }

    fun size(key: String): Long =
        redisTemplate.opsForValue().size(key)

    fun multiSet(maps: Map<String, String>) {
        redisTemplate.opsForValue().multiSet(maps)
    }

    fun multiSetIfAbsent(maps: Map<String, String>): Boolean =
        redisTemplate.opsForValue().multiSetIfAbsent(maps)

    fun incrBy(key: String, increment: Long): Long =
        redisTemplate.opsForValue().increment(key, increment)

    fun incrByFloat(key: String, increment: Double): Double =
        redisTemplate.opsForValue().increment(key, increment)

    fun append(key: String, value: String): Int? =
        redisTemplate.opsForValue().append(key, value)

    // ==================== Hash 相关操作 ====================

    fun hGet(key: String, field: String): Any? =
        redisTemplate.opsForHash<String, Any>().get(key, field)

    fun hGetAll(key: String): Map<Any, Any> =
        redisTemplate.opsForHash<String, Any>().entries(key) as Map<Any, Any>

    fun hMultiGet(key: String, fields: Collection<Any>): List<Any>? =
        redisTemplate.opsForHash<String, Any>().multiGet(key, fields as Collection<String>)

    fun hPut(key: String, hashKey: String, value: Any) {
        redisTemplate.opsForHash<String, Any>().put(key, hashKey, value)
    }

    fun hPutAll(key: String, maps: Map<String, Any>) {
        redisTemplate.opsForHash<String, Any>().putAll(key, maps)
    }

    fun hPutIfAbsent(key: String, hashKey: String, value: Any): Boolean =
        redisTemplate.opsForHash<String, Any>().putIfAbsent(key, hashKey, value)

    fun hDelete(key: String, vararg fields: Any): Long =
        redisTemplate.opsForHash<String, Any>().delete(key, *fields)

    fun hExists(key: String, field: String): Boolean =
        redisTemplate.opsForHash<String, Any>().hasKey(key, field)

    fun hIncrBy(key: String, field: Any, increment: Long): Long =
        redisTemplate.opsForHash<String, Any>().increment(key, field as String, increment)

    fun hIncrByFloat(key: String, field: Any, delta: Double): Double =
        redisTemplate.opsForHash<String, Any>().increment(key, field as String, delta)

    fun hKeys(key: String): Set<Any> =
        redisTemplate.opsForHash<String, Any>().keys(key)

    fun hSize(key: String): Long =
        redisTemplate.opsForHash<String, Any>().size(key)

    fun hValues(key: String): List<Any> =
        redisTemplate.opsForHash<String, Any>().values(key)

    fun hScan(key: String, options: ScanOptions): Cursor<Map.Entry<String, Any?>> =
        redisTemplate.opsForHash<String, Any>().scan(key, options)

    // ==================== List 相关操作 ====================

    fun lIndex(key: String, index: Long): String? =
        redisTemplate.opsForList().index(key, index)

    fun lRange(key: String, start: Long, end: Long): List<String>? =
        redisTemplate.opsForList().range(key, start, end)

    fun lLeftPush(key: String, value: String): Long? =
        redisTemplate.opsForList().leftPush(key, value)

    fun lLeftPushAll(key: String, vararg value: String): Long? =
        redisTemplate.opsForList().leftPushAll(key, *value)

    fun lLeftPushAll(key: String, value: Collection<String>): Long? =
        redisTemplate.opsForList().leftPushAll(key, value)

    fun lLeftPushIfPresent(key: String, value: String): Long? =
        redisTemplate.opsForList().leftPushIfPresent(key, value)

    fun lLeftPush(key: String, pivot: String, value: String): Long? =
        redisTemplate.opsForList().leftPush(key, pivot, value)

    fun lRightPush(key: String, value: String): Long? =
        redisTemplate.opsForList().rightPush(key, value)

    fun lRightPushAll(key: String, vararg value: String): Long? =
        redisTemplate.opsForList().rightPushAll(key, *value)

    fun lRightPushAll(key: String, value: Collection<String>): Long? =
        redisTemplate.opsForList().rightPushAll(key, value)

    fun lRightPushIfPresent(key: String, value: String): Long? =
        redisTemplate.opsForList().rightPushIfPresent(key, value)

    fun lRightPush(key: String, pivot: String, value: String): Long? =
        redisTemplate.opsForList().rightPush(key, pivot, value)

    fun lSet(key: String, index: Long, value: String) {
        redisTemplate.opsForList().set(key, index, value)
    }

    fun lLeftPop(key: String): String? =
        redisTemplate.opsForList().leftPop(key)

    fun lBLeftPop(key: String, timeout: Long, unit: TimeUnit): String? =
        redisTemplate.opsForList().leftPop(key, timeout, unit)

    fun lRightPop(key: String): String? =
        redisTemplate.opsForList().rightPop(key)

    fun lBRightPop(key: String, timeout: Long, unit: TimeUnit): String? =
        redisTemplate.opsForList().rightPop(key, timeout, unit)

    fun lRightPopAndLeftPush(sourceKey: String, destinationKey: String): String? =
        redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey)

    fun lBRightPopAndLeftPush(
        sourceKey: String,
        destinationKey: String,
        timeout: Long,
        unit: TimeUnit
    ): String? =
        redisTemplate.opsForList().rightPopAndLeftPush(sourceKey, destinationKey, timeout, unit)

    fun lRemove(key: String, index: Long, value: String): Long? =
        redisTemplate.opsForList().remove(key, index, value)

    fun lTrim(key: String, start: Long, end: Long) {
        redisTemplate.opsForList().trim(key, start, end)
    }

    fun lLen(key: String): Long? =
        redisTemplate.opsForList().size(key)

    // ==================== Set 相关操作 ====================

    fun sAdd(key: String, vararg values: String): Long? =
        redisTemplate.opsForSet().add(key, *values)

    fun sRemove(key: String, vararg values: Any): Long? =
        redisTemplate.opsForSet().remove(key, *values)

    fun sPop(key: String): String? =
        redisTemplate.opsForSet().pop(key)

    fun sMove(key: String, value: String, destKey: String): Boolean =
        redisTemplate.opsForSet().move(key, value, destKey)

    fun sSize(key: String): Long? =
        redisTemplate.opsForSet().size(key)

    fun sIsMember(key: String, value: Any): Boolean =
        redisTemplate.opsForSet().isMember(key, value)

    fun sIntersect(key: String, otherKey: String): Set<String>? =
        redisTemplate.opsForSet().intersect(key, otherKey)

    fun sIntersect(key: String, otherKeys: Collection<String>): Set<String>? =
        redisTemplate.opsForSet().intersect(key, otherKeys)

    fun sIntersectAndStore(key: String, otherKey: String, destKey: String): Long? =
        redisTemplate.opsForSet().intersectAndStore(key, otherKey, destKey)

    fun sIntersectAndStore(key: String, otherKeys: Collection<String>, destKey: String): Long? =
        redisTemplate.opsForSet().intersectAndStore(key, otherKeys, destKey)

    fun sUnion(key: String, otherKeys: String): Set<String>? =
        redisTemplate.opsForSet().union(key, otherKeys)

    fun sUnion(key: String, otherKeys: Collection<String>): Set<String>? =
        redisTemplate.opsForSet().union(key, otherKeys)

    fun sUnionAndStore(key: String, otherKey: String, destKey: String): Long? =
        redisTemplate.opsForSet().unionAndStore(key, otherKey, destKey)

    fun sUnionAndStore(key: String, otherKeys: Collection<String>, destKey: String): Long? =
        redisTemplate.opsForSet().unionAndStore(key, otherKeys, destKey)

    fun sDifference(key: String, otherKey: String): Set<String>? =
        redisTemplate.opsForSet().difference(key, otherKey)

    fun sDifference(key: String, otherKeys: Collection<String>): Set<String>? =
        redisTemplate.opsForSet().difference(key, otherKeys)

    fun sDifference(key: String, otherKey: String, destKey: String): Long? =
        redisTemplate.opsForSet().differenceAndStore(key, otherKey, destKey)

    fun sDifference(key: String, otherKeys: Collection<String>, destKey: String): Long? =
        redisTemplate.opsForSet().differenceAndStore(key, otherKeys, destKey)

    fun setMembers(key: String): Set<String>? =
        redisTemplate.opsForSet().members(key)

    fun sRandomMember(key: String): String? =
        redisTemplate.opsForSet().randomMember(key)

    fun sRandomMembers(key: String, count: Long): List<String>? =
        redisTemplate.opsForSet().randomMembers(key, count)

    fun sDistinctRandomMembers(key: String, count: Long): Set<String>? =
        redisTemplate.opsForSet().distinctRandomMembers(key, count)

    fun sScan(key: String, options: ScanOptions): Cursor<String> =
        redisTemplate.opsForSet().scan(key, options)

    // ==================== ZSet 相关操作 ====================

    fun zAdd(key: String, value: String, score: Double): Boolean? =
        redisTemplate.opsForZSet().add(key, value, score)

    fun zAdd(key: String, values: Set<ZSetOperations.TypedTuple<String>>): Long? =
        redisTemplate.opsForZSet().add(key, values)

    fun zRemove(key: String, vararg values: Any): Long? =
        redisTemplate.opsForZSet().remove(key, *values)

    fun zIncrementScore(key: String, value: String, delta: Double): Double? =
        redisTemplate.opsForZSet().incrementScore(key, value, delta)

    fun zRank(key: String, value: Any): Long? =
        redisTemplate.opsForZSet().rank(key, value)

    fun zReverseRank(key: String, value: Any): Long? =
        redisTemplate.opsForZSet().reverseRank(key, value)

    fun zRange(key: String, start: Long, end: Long): Set<String>? =
        redisTemplate.opsForZSet().range(key, start, end)

    fun zRangeWithScores(key: String, start: Long, end: Long): Set<ZSetOperations.TypedTuple<String>>? =
        redisTemplate.opsForZSet().rangeWithScores(key, start, end)

    fun zRangeByScore(key: String, min: Double, max: Double): Set<String>? =
        redisTemplate.opsForZSet().rangeByScore(key, min, max)

    fun zRangeByScoreWithScores(key: String, min: Double, max: Double): Set<ZSetOperations.TypedTuple<String>>? =
        redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max)

    fun zRangeByScoreWithScores(
        key: String,
        min: Double,
        max: Double,
        start: Long,
        end: Long
    ): Set<ZSetOperations.TypedTuple<String>>? =
        redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, start, end)

    fun zReverseRange(key: String, start: Long, end: Long): Set<String>? =
        redisTemplate.opsForZSet().reverseRange(key, start, end)

    fun zReverseRangeWithScores(key: String, start: Long, end: Long): Set<ZSetOperations.TypedTuple<String>>? =
        redisTemplate.opsForZSet().reverseRangeWithScores(key, start, end)

    fun zReverseRangeByScore(key: String, min: Double, max: Double): Set<String>? =
        redisTemplate.opsForZSet().reverseRangeByScore(key, min, max)

    fun zReverseRangeByScoreWithScores(key: String, min: Double, max: Double): Set<ZSetOperations.TypedTuple<String>>? =
        redisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, min, max)

    fun zReverseRangeByScore(key: String, min: Double, max: Double, start: Long, end: Long): Set<String>? =
        redisTemplate.opsForZSet().reverseRangeByScore(key, min, max, start, end)

    fun zCount(key: String, min: Double, max: Double): Long? =
        redisTemplate.opsForZSet().count(key, min, max)

    fun zSize(key: String): Long? =
        redisTemplate.opsForZSet().size(key)

    fun zZCard(key: String): Long? =
        redisTemplate.opsForZSet().zCard(key)

    fun zScore(key: String, value: Any): Double? =
        redisTemplate.opsForZSet().score(key, value)

    fun zRemoveRange(key: String, start: Long, end: Long): Long? =
        redisTemplate.opsForZSet().removeRange(key, start, end)

    fun zRemoveRangeByScore(key: String, min: Double, max: Double): Long? =
        redisTemplate.opsForZSet().removeRangeByScore(key, min, max)

    fun zUnionAndStore(key: String, otherKey: String, destKey: String): Long? =
        redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey)

    fun zUnionAndStore(key: String, otherKeys: Collection<String>, destKey: String): Long? =
        redisTemplate.opsForZSet().unionAndStore(key, otherKeys, destKey)

    fun zIntersectAndStore(key: String, otherKey: String, destKey: String): Long? =
        redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey)

    fun zIntersectAndStore(key: String, otherKeys: Collection<String>, destKey: String): Long? =
        redisTemplate.opsForZSet().intersectAndStore(key, otherKeys, destKey)

    fun zScan(key: String, options: ScanOptions): Cursor<ZSetOperations.TypedTuple<String>> =
        redisTemplate.opsForZSet().scan(key, options)
}