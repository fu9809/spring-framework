package com.fu.test.test;

import com.fu.test.config.AppConfig;
import com.fu.test.ioc.A;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * test
 *
 * @author Fu
 * @date 2021/6/10 11:20
 */
public class Test {
	public static void main(String[] args) {
		/**
		 * 实例化 spring 容器的大体流程：
		 *  1. 解析配置类
		 *  2. 扫描配置类配置的信息
		 *  3. 实例化 springBean （实例化对象的方式：new，反射，序列化，克隆）
		 */
		ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

		System.out.println(ac.getBean(A.class));
	}
}
