package com.fu.factorybean;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * factorybean
 *
 * @author Fu
 * @date 2021/6/16 10:43
 */
@Component("customerFactoryBean")
public class CustomerFactoryBean implements FactoryBean<Customer> {

	@Override
	public Customer getObject() throws Exception {
		Customer customer = new Customer();
		customer.setUid(UUID.randomUUID().toString());
		customer.setMsg("msg");
		customer.setDate(LocalDateTime.now());
		return customer;
	}

	@Override
	public Class<?> getObjectType() {
		return null;
	}

	@Override
	public boolean isSingleton() {
		return FactoryBean.super.isSingleton();
	}
}
