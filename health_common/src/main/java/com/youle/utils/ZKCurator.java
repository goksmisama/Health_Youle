package com.youle.utils;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCurator {
	 
	
	private CuratorFramework client = null;
	
	
	
	final static Logger log = LoggerFactory.getLogger(ZKCurator.class);
	
	public ZKCurator(CuratorFramework client) {
		this.client = client;
	}
	
	/*
	 * 初始化操作
	 */
	public void init() {
		client = client.usingNamespace("zk-curator-connector");
	}
	
	/*判断zk是否连接*/
	public boolean isZkAlive() {
		return client.isStarted();
	}

}
