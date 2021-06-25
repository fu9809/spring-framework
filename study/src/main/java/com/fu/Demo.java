package com.fu;

import com.fu.beanFactoryPostProcessor.MyBeanFactoryPostProcessor;
import com.fu.beanPostProcessor.IndexDao;
import com.fu.config.DemoConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * demo
 *
 * @author Fu`
 * @date 2021/6/18 15:47
 */
public class Demo {
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

		context.register(DemoConfig.class);

		context.addBeanFactoryPostProcessor(new MyBeanFactoryPostProcessor());

		context.refresh();

		IndexDao indexDao = (IndexDao) context.getBean("indexDao");
		System.out.println(indexDao.getApplicationContext());
	}
}
