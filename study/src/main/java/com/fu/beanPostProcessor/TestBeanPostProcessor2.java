package com.fu.beanPostProcessor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * bean 后置处理器
 *
 * @author Fu
 * @date 2021/6/18 15:10
 */
@Component
public class TestBeanPostProcessor2 implements BeanPostProcessor, PriorityOrdered {

	private static final int order = 99;

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if ("indexDao".equals(beanName)) {
			System.out.println("Bean初始化之前执行。。。Order: " + order);
		}
		// 一定要把 bean 返回出去，否则Spring容器中就不存在这个bean了
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if ("indexDao".equals(beanName)) {
			System.out.println("Bean初始化之后执行。。。Order: " + order);
		}
		return bean;
	}

	@Override
	public int getOrder() {
		// 值越小，越先执行
		return order;
	}
}
