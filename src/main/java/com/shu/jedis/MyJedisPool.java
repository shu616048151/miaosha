package com.shu.jedis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * jedis池
 */
public class MyJedisPool {
	private static JedisPool jedisPool=null;
	static{
			if(jedisPool == null) {
				JedisPoolConfig config = new JedisPoolConfig();
		        // 设置最大连接数
		        config.setMaxTotal(200);
		        // 设置最大空闲数
		        config.setMaxIdle(8);
		        // 设置最大等待时间
		        config.setMaxWaitMillis(1000 * 100);
		        // 在borrow一个jedis实例时，是否需要验证，若为true，则所有jedis实例均是可用的
		        config.setTestOnBorrow(true);
				jedisPool = new JedisPool(config,"127.0.0.1",6379,3000);
			}
		}
	public static Jedis getJedis(){
		//使用jedis的连接池，提高性能
		return jedisPool.getResource();
	}
}
