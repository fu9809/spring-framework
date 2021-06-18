package com.fu.beanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

/**
 * test
 *
 * @author Fu
 * @date 2021/6/18 15:42
 */
@Repository
public class IndexDao implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public IndexDao() {
		System.out.println("IndexDao...执行构造方法，创建IndexDao对象...");
	}

	@PostConstruct
	public void postConstruct() {
		System.out.println("IndexDao...执行Bean的初始化方法，初始化Bean对象...");
	}

	public void demo() {
		System.out.println("indexDao...demo...");
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
