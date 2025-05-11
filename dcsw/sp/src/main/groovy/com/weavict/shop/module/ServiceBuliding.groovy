package com.weavict.shop.module

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.context.support.FileSystemXmlApplicationContext
import org.springframework.web.context.support.WebApplicationContextUtils

import jakarta.servlet.http.HttpServletRequest

class ServiceBuliding
{
	static Object theService(String name,HttpServletRequest request)
	{
		ApplicationContext ctx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(request.getSession().getServletContext());
		return ctx.getBean(name);
	}
	
	static Object theClassPathService(String classPathXml)
	{
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
	}
	
	static Object theTestService(String name)
	{
		ApplicationContext ctx = new FileSystemXmlApplicationContext("WebContent/WEB-INF/classes/applicationContext.xml");
		return ctx.getBean(name);
	}
}
