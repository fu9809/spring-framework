/*
 * Copyright 2002-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.context.annotation;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.function.Supplier;

/**
 * Standalone application context, accepting <em>component classes</em> as input &mdash;
 * in particular {@link Configuration @Configuration}-annotated classes, but also plain
 * {@link org.springframework.stereotype.Component @Component} types and JSR-330 compliant
 * classes using {@code javax.inject} annotations.
 *
 * <p>Allows for registering classes one by one using {@link #register(Class...)}
 * as well as for classpath scanning using {@link #scan(String...)}.
 *
 * <p>In case of multiple {@code @Configuration} classes, {@link Bean @Bean} methods
 * defined in later classes will override those defined in earlier classes. This can
 * be leveraged to deliberately override certain bean definitions via an extra
 * {@code @Configuration} class.
 *
 * <p>See {@link Configuration @Configuration}'s javadoc for usage examples.
 *
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 3.0
 * @see #register
 * @see #scan
 * @see AnnotatedBeanDefinitionReader
 * @see ClassPathBeanDefinitionScanner
 * @see org.springframework.context.support.GenericXmlApplicationContext
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

	/**
	 * 带注释的 Bean 定义读取器
	 * 定义一个 读取器，构造方法里还会初始化一个注册器BeanDefinitionRegistry
	 * 		（其实就是 AnnotationConfigApplicationContext 本身，因为其实现了BeanDefinitionRegistry接口）
	 * 作用：把扫描到的类，解析并包装成 BeanDefinition 对象；（之后会使用注册器，将 BD 存入 BeanDefinitionMap 中）
	 *   用于对特定注解（如@Service、@Repository)的类进行读取转化成BeanDefinition对象，
	 *   	(BeanDefinition是 Spring中极其重要的一个概念，它存储了bean对象的所有特征信息，如是否单例，是否懒加载，factoryBeanName等)
	 *
	 *  通过读取器读取，是把普通类变成springBean 的方法之一；一般外部对象，即用户自定义的对象，使用这种方式注册springBean
	 */
	private final AnnotatedBeanDefinitionReader reader;

	/**
	 * 类路径 Bean 定义扫描器
	 * 作用：用于扫描在指定路径下所有的Bean
	 *
	 * 	虽然这是个扫描器，但是完成扫描工作的并不是此 scanner，
	 * 	是spring自己 new 的一个对象 ClassPathBeanDefinitionScanner 完成的，
	 * 		扫描工作是 org.springframework.core.io.support.PathMatchingResourcePatternResolver 这个类完成，
	 * 		通过匹配路径，获取文件夹下文件，扫描文件来完成扫描的
	 *
	 * 	这个 scanner 对象只是为了我们能够在外部调用 AnnotationConfigApplicationContext 对象的 scan() 方法
	 *
	 *    使用 scanner 扫描出来的类，也是通过 new ScannedGenericBeanDefinition 注册到spring容器的
	 */
	private final ClassPathBeanDefinitionScanner scanner;


	/**
	 * 创建一个需要填充的新 AnnotationConfigApplicationContext.
	 *
	 * Create a new AnnotationConfigApplicationContext that needs to be populated
	 * through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
	 */
	public AnnotationConfigApplicationContext() {
		/**
		 * 初始化一个 BD 读取器；
		 * 会把spring内部的 六大对象 创建并添加到容器中
 		 */
		this.reader = new AnnotatedBeanDefinitionReader(this);
		/**
		 * 初始化一个 BD 扫描器
		 * 用于扫描指定路径下的 BD
		 */
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext with the given DefaultListableBeanFactory.
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 */
	public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);
		this.reader = new AnnotatedBeanDefinitionReader(this);
		this.scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, deriving bean definitions
	 * from the given component classes and automatically refreshing the context.
	 * @param componentClasses one or more component classes &mdash; for example,
	 * {@link Configuration @Configuration} classes
	 *
	 */
	/**
     * 创建一个新的 AnnotationConfigApplicationContext，从给定的组件类派生 bean 定义并自动刷新上下文。
  	 * @param componentClasses  一个或多个组件类——例如，配置了 @Configuration 的类
	 */
	public AnnotationConfigApplicationContext(Class<?>... componentClasses) {
		/**
			初始化 ApplicationContext 容器
			1. 因为该类有父类，先调用父类的无参构造方法，然后调用自己的无参构造方法；父类的构造方法所做的事情
				1.1 实例化 BeanFactory 工厂，用于生成Bean对象
			2. 在自己的构造方法中初始化一个读取器和扫描器
				2.1 初始化BeanDefinition读取器AnnotatedBeanDefinitionReader，用于读取加了注解的类，将类读取并解析为BeanDefinition对象
		 			2.1.1 会将spring内部的六大后置处理对象添加到spring容器中
				2.2 初始化BeanDefinition扫描器ClassPathBeanDefinitionScanner，用于扫描指定路径下加了注解的类
		 			2.2.1 注册默认过滤器
		 			2.2.2 设置环境
		 			2.2.3 初始化资源加载器
		*/
		this();
		/**
			注册组件配置类
			1. 将自定义的配置类注册到spring容器中，可配置多个；这里只会将配置类注册到 spring容器
		*/
		register(componentClasses);
		/**
			刷新 spring，完善spring容器
			1. 初始化spring容器；包括：设置启动事件；设置启动状态；初始化任何占位符属性源；
			2. 初始化刷新spring内部的bean
		 	3. 将spring扫描到的类也加入spring容器中
		*/
		refresh();
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, scanning for components
	 * in the given packages, registering bean definitions for those components,
	 * and automatically refreshing the context.
	 * @param basePackages the packages to scan for component classes
	 */
	public AnnotationConfigApplicationContext(String... basePackages) {
		this();
		scan(basePackages);
		refresh();
	}


	/**
	 * Propagate the given custom {@code Environment} to the underlying
	 * {@link AnnotatedBeanDefinitionReader} and {@link ClassPathBeanDefinitionScanner}.
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		this.reader.setEnvironment(environment);
		this.scanner.setEnvironment(environment);
	}

	/**
	 * Provide a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader}
	 * and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * <p>Default is {@link AnnotationBeanNameGenerator}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
	 * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
	 * @see AnnotationBeanNameGenerator
	 * @see FullyQualifiedAnnotationBeanNameGenerator
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		this.reader.setBeanNameGenerator(beanNameGenerator);
		this.scanner.setBeanNameGenerator(beanNameGenerator);
		getBeanFactory().registerSingleton(
				AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
	}

	/**
	 * Set the {@link ScopeMetadataResolver} to use for registered component classes.
	 * <p>The default is an {@link AnnotationScopeMetadataResolver}.
	 * <p>Any call to this method must occur prior to calls to {@link #register(Class...)}
	 * and/or {@link #scan(String...)}.
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		this.reader.setScopeMetadataResolver(scopeMetadataResolver);
		this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
	}


	//---------------------------------------------------------------------
	// Implementation of AnnotationConfigRegistry
	//---------------------------------------------------------------------

	/**
	 * 注册一个或多个要处理的组件类.
	 * Register one or more component classes to be processed.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param componentClasses one or more component classes &mdash; for example,
	 * {@link Configuration @Configuration} classes
	 * @see #scan(String...)
	 * @see #refresh()
	 */
	@Override
	public void register(Class<?>... componentClasses) {
		Assert.notEmpty(componentClasses, "At least one component class must be specified");
		this.reader.register(componentClasses);
	}

	/**
	 * Perform a scan within the specified base packages.
	 * <p>Note that {@link #refresh()} must be called in order for the context
	 * to fully process the new classes.
	 * @param basePackages the packages to scan for component classes
	 * @see #register(Class...)
	 * @see #refresh()
	 */
	@Override
	public void scan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		this.scanner.scan(basePackages);
	}


	//---------------------------------------------------------------------
	// Adapt superclass registerBean calls to AnnotatedBeanDefinitionReader
	//---------------------------------------------------------------------

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass,
			@Nullable Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {

		this.reader.registerBean(beanClass, beanName, supplier, customizers);
	}

}
