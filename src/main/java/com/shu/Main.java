package com.shu;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.shu.jedis.MyJedisPool;
import redis.clients.jedis.Jedis;
/**
 * redis乐观锁的方式实现秒杀
 * @author shuxibing
 *
 */
public class Main {
	public static void main(String[] args) {
		String key="wecash";
		long startTime=System.currentTimeMillis();
		//初始化秒杀的数量
		initNum(key, 200);
		//当前的秒杀人数,线程池的效率高一些
		long endTime=startManyThread(key, 1000);
		System.out.println("总共执行时间："+(endTime-startTime)+"ms");
	}
	/**
	 * 
	 * @param key 秒杀的商品
	 * @param num 秒杀的数量
	 */
	public static void initNum(String key,int num){
		Jedis jedis= MyJedisPool.getJedis();
		jedis.set(key, String.valueOf(num));
		jedis.close();
	}
	/**
	 * 使用线程池的方式
	 * @param key 秒杀的商品
	 * @param n 秒杀的人数
	 *
	 */
	public static long startManyThread(String key,int n) {
		ExecutorService executor = Executors.newFixedThreadPool(30);
		if (n>0) {
			for (int i = 0; i < n; i++) {
				String id=UUID.randomUUID().toString().replace("-", "");
				executor.execute(new com.shu.MyThread((i+1)+"用户ID:"+id, key));
			}
		}
		executor.shutdown();
		//确认线程是否完全结束
		while(true){
			if (executor.isTerminated()) {
				break;
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return System.currentTimeMillis();
	}

}
