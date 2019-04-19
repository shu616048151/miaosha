package com.shu.test;

import org.apache.commons.lang3.StringUtils;

import com.shu.jedis.MyJedisPool;

import redis.clients.jedis.Jedis;
/**
 * 分布式锁
 * @author Administrator
 *
 */
public class MyThread implements Runnable{
	final String key="lock";
	final long timeout=100;
	Jedis jedis=MyJedisPool.getJedis();
	private String productKey;
	private String customerId;
	
	public MyThread( String customerId,String productKey) {
		super();
		this.productKey = productKey;
		this.customerId = customerId;
	}
	public void run() {
		long end=System.currentTimeMillis()+timeout;
		boolean lock=lock(key, String.valueOf(end));
		int num=Integer.parseInt(jedis.get(productKey));
		if (lock&&num>0&&num<=200) {
		
				jedis.decr(productKey);
				System.out.println(customerId+"抢购成功,剩余数："+num);
				unlock(key, String.valueOf(end));
		}else {
			if (num<=0) {				
				System.out.println(customerId+"商品抢购完了");
			}else {
				System.out.println(customerId+"抢购失败");
			}
		}
		jedis.close();
	}

	/**
	 * 上锁
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean lock(String key,String value){
	    if(jedis.setnx(key, value)==1){//setNX 返回boolean
	        return true;
	    }
	    //如果锁超时 ***
	    String currentValue = jedis.get(key);
	    if(!StringUtils.isEmpty(currentValue) && Long.parseLong(currentValue)<System.currentTimeMillis()){
	        //获取上一个锁的时间
	        String oldvalue  = jedis.getSet(key,value);
	        if(!StringUtils.isEmpty(oldvalue)&&oldvalue.equals(currentValue)){
	            return true;
	        }
	    }
	    return false;
	}
	/***
	 * 解锁
	 * @param key
	 * @param value
	 * @return
	 */
	public void unlock(String key,String value){
	    try {
	        String currentValue = jedis.get(key);
	        if(!StringUtils.isEmpty(currentValue)&&currentValue.equals(value)){
	            jedis.del(key);
	        }
	    } catch (Exception e) {
	        System.out.println("解锁异常");
	    }
	}


}
