package com.baizhiedu.cache;

import com.baizhiedu.util.JedisUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.ibatis.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * @author Serendipity
 * @description
 * @date 2024-12-17 19:57
 **/
public class RedisCache implements Cache {

    private static final Logger log = LoggerFactory.getLogger(RedisCache.class);
    private final String id;
    private final HashMap<Object, Object> internalCache = new HashMap<>();

    /**
     * 完全照猫画虎，让 MyBatic 传入id
     */
    public RedisCache(String id) {
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    /**
     * 向 redis 存储数据
     */
    @Override
    public void putObject(Object key, Object value) {

        log.info("");
        log.debug("putObject, key: {}, value: {}", key, value);
        Jedis jedis = JedisUtils.getJedis();
        jedis.set(SerializationUtils.serialize((Serializable) key), SerializationUtils.serialize((Serializable) value));
    }

    /**
     * 从 redis 获取缓存数据
     */
    @Override
    public Object getObject(Object key) {
        log.debug("getObject, key: {}", key);
        Jedis jedis = JedisUtils.getJedis();
        byte[] bytes = jedis.get(SerializationUtils.serialize((Serializable) key));
        if (bytes == null) {
            return null;
        }
        return SerializationUtils.deserialize(bytes);
    }

    /**
     * 从 redis 删除缓存数据
     */
    @Override
    public Object removeObject(Object key) {
        log.debug("removeObject, key: {}", key);
        Jedis jedis = JedisUtils.getJedis();
        byte[] serializeKey = SerializationUtils.serialize((Serializable) key);
        byte[] bytes = jedis.get(serializeKey);
        if (bytes == null) {
            return null;
        }
        jedis.del(serializeKey);
        return SerializationUtils.deserialize(bytes);
    }

    /**
     * 从 redis 清空缓存数据
     */
    @Override
    public void clear() {
        log.debug("clear...");

        Jedis jedis = JedisUtils.getJedis();
        jedis.flushDB();
    }

    /**
     * 获取缓存数量
     */
    @Override
    public int getSize() {
        log.debug("getSize...");
        Jedis jedis = JedisUtils.getJedis();
        return jedis.dbSize().intValue();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return null;
    }


}
