package com.youle.utils;

import java.util.concurrent.CountDownLatch;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/*
 * 分布式锁的工具类
 */
public class DistributedLock {
	
	//ZK的客户端
	private CuratorFramework client = null;
	
	final static Logger log = LoggerFactory.getLogger(DistributedLock.class);
	
	//用于挂起当前的请求 并且等待上一个分布式锁被释放
	private static CountDownLatch zkLocakLatch = new CountDownLatch(1);
	
	//分布式锁的总节点
	private static final String ZK_LOCK_PROJECT = "youyue_locks";
	
	//分布式锁的节点
	private static final String DISTRIBUTED_LOCK = "distributed_lock";
	
	//构造函数
	public DistributedLock(CuratorFramework cliet) {
		this.client = cliet;
	}
	
	//初始化锁
	public void init() throws Exception {
		//使用命名空间
		client = client.usingNamespace("ZKLocks-Namespance");
		/*
		 * 创建zk锁的总结点
		 *   ZKLocks-Namespance
		 *     | 
		 *     youyue_locks
		 *     		|
		 *          distributed_lock
		 */
		try{		//checkExists()是个接口用于判断ZK中是否存在节点
			if(client.checkExists().forPath("/"+ZK_LOCK_PROJECT) == null) {
				client.create().//创建节点
				creatingParentsIfNeeded(). //递归创建，如果没有父节点自动创建父节点
				withMode(CreateMode.PERSISTENT).//指定节点类型为持久节点    PERSISTENT  临时节点EPHEMERAL
				withACL(Ids.OPEN_ACL_UNSAFE). //指定节点的权限  
				//如果想指定权限  ACL为 digest:wangsaichao:123456:cdrwa 
				//withACL(Collections.singletonList(new ACL(ZooDefs.Perms.ALL, new Id("digest", "wangsaichao:G2RdrM8e0u0f1vNCj/TI99ebRMw="))))
				forPath("/"+ZK_LOCK_PROJECT); //总结点的路径名
			}
			//针对zookeeper的分布式锁的节点 创建相应的watch事件
			addWatcherToLock("/"+ZK_LOCK_PROJECT);
		}catch (Exception e) {
			log.error("客户端连接zookeeper服务器端错误，请重试...");
		}
		
	}
	
	//创建watcher监听
	public void addWatcherToLock(String path) throws Exception {
		final PathChildrenCache cache = new PathChildrenCache(client, path, true);//PathChildrenCache监听子节点的增删改会触发事件， 三个参数  客户端  ，监听的节点，true开启
		cache.start(StartMode.POST_INITIALIZED_EVENT);  //StartMode：初始化方式       POST_INITIALIZED_EVENT：异步初始化。初始化后会触发事件
		cache.getListenable().addListener(new PathChildrenCacheListener() {//添加监听器
			
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {//通过监听器判断子节点是否存在  不存在就是上一个会话已经断开连接
					String path = event.getData().getPath();
					//上一个会话已经释放该会话已经断开  节点路径为 path
					log.info("上一个会话已经释放该会话已经断开  节点路径为" +path);
					if(path.contains(DISTRIBUTED_LOCK)) { //判断节点路径是否包含分布式锁子节点路径
						log.info("释放计数器，让当前请求来获得分布式锁...");
						zkLocakLatch.countDown();  //计数器减1 允许其他线程执行
					}
			
				}
				
			}
		});
	}
	//获得分布式锁
	public void getLock() {
		//使用死循环 当前请求获得锁之后才会跳出循环
		while(true) {
			try {
				client.create().
				creatingParentsIfNeeded().
				withMode(CreateMode.EPHEMERAL).
				withACL(Ids.OPEN_ACL_UNSAFE).
				forPath("/"+ZK_LOCK_PROJECT+"/"+DISTRIBUTED_LOCK);
				log.info("获得分布式锁成功...");
				return;
			}catch (Exception e) {
				// TODO: handle exception
				log.info("分布式锁获得失败...");
				try {
					//如果没有得到锁 需要重新设置同步资源值
					if(zkLocakLatch.getCount() <= 0) {
						zkLocakLatch = new CountDownLatch(1);
					}
					//阻塞线程
					zkLocakLatch.await();
				}catch (Exception exception) {
					// TODO: handle exception
					exception.printStackTrace();
				}
			}
		}
	}
	//释放锁
	public boolean releaseLock() {
		try {
			if(client.checkExists().forPath("/"+ZK_LOCK_PROJECT+"/"+DISTRIBUTED_LOCK) != null) {
				client.delete().forPath("/"+ZK_LOCK_PROJECT+"/"+DISTRIBUTED_LOCK);
			}
		}catch (Exception e) {
			// TODO: handle exception
			return false;
		}
		log.info("分布式锁释放完毕..");
		return true;
	}
}
