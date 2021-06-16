package com.fu.factorybean;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * test
 *
 * @author Fu
 * @date 2021/6/16 10:51
 */
public class Test {

	/**
	 *  CustomerFactoryBean 实现了 FactoryBean 接口
	 *  直接通过 bean 名称获取的对象是其 getObject() 返回的对象，要获取 CustomerFactoryBean 对象本身，需要在名字前加 '&'
	 */
	public static void main(String[] args) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.fu.factorybean");
		context.refresh();

		/**
		 * 通过以下这两种方法，均可以获取到 CustomerFactoryBean 对象
		 */
		CustomerFactoryBean customerFactoryBean = (CustomerFactoryBean) context.getBean("&customerFactoryBean");
		System.out.println(customerFactoryBean);
		CustomerFactoryBean customerFactoryBean1 = context.getBean(CustomerFactoryBean.class);
		System.out.println(customerFactoryBean1);

		/**
		 * 获取 Customer 对象，只能使用这种方式获取
		 */
		Customer customer = (Customer) context.getBean("customerFactoryBean");
		System.out.println(customer);

		// 抛出异常：NoSuchBeanDefinitionException: No qualifying bean of type 'com.fu.factorybean.Customer' available
		Customer customer1 = context.getBean(Customer.class);
		System.out.println(customer1);
	}
}
