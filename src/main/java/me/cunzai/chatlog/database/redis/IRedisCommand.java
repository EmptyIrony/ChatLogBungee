package me.cunzai.chatlog.database.redis;

import redis.clients.jedis.Jedis;

public interface IRedisCommand<T> {

    /**
     * @param redis
     * @return
     */
    T execute(Jedis redis);

}

