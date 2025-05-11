package com.weavict.weichat;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

//@WebListener
public class TokenManagerListener implements ServletContextListener{

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		//WEB容器 初始化时调用
		TokenManager.init(Tokens.getTheAppid(""), Tokens.getTheSecret(""));
		System.out.println("***********Shop TokenManager begin**************");
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		//WEB容器  关闭时调用
		TokenManager.destroyed();
	}
}

