package com.shu.thread;

import java.util.List;

import com.shu.jedis.MyJedisPool;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

/**
 * 乐观锁方式的线程
 * @author shuxibing
 */
public class OptiLockThread extends Thread{
	//从连接池中得到jedis
	private Jedis Jedis= MyJedisPool.getJedis();
	private String customerName;
	private String key;
	private final  String result="秒杀结果";
	public OptiLockThread(String customerName, String key) {
		super();
		this.customerName = customerName;
		this.key=key;
	}
	/**
	 * 因为redis是单线层的，它的执行顺序是按照队列顺序进行一对一执行
	 * 本秒杀采用redis的事务乐观锁的方式进行实现,事务的流程和mysql基本类似
	 * 
	 */
	@Override
	public void run() {
		//实现乐观锁，当exec时，如果监视的key从调用watch后发生过变化，则事务会失败
		Jedis.watch(key);
		//得到当前的商品剩余数量
		int num=Integer.parseInt(Jedis.get(key));
		if (num>0&&num<=200) {
			//开启事务
			Transaction transaction = Jedis.multi();
			//在事务管理下，将商品的数量减一
			transaction.decr(key);
			//提交事务,返回一个执行的集合
			List<Object> list = transaction.exec();
			if (list!=null&&list.size()>0) {
				for (Object object : list) {
					System.out.println(customerName+"秒杀成功商品:"+object.toString()+"  当前商品剩余数:"+(200-num+1));
				}
			}else {
				System.out.println(customerName+"秒杀失败");
			}
		}else {
			System.out.println(customerName+"商品已经秒杀完了");
		}
		//关闭jedis连接
		Jedis.close();
		
	}
	
}
