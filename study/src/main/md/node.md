# Spring

## 为什么学
1. 项目中遇到的问题：
   - Service类中的一些方法上，加上 @Async 或 @Transactional 注解之后，启动项目会报循环依赖的错误
   - @Transactional 注解失效的原因
2. 为了提升技术，学习spring的架构，设计模式，IOC，AOP原理

## springBean

spring中的Bean 用 BeanDefinition 表示，相当于 java中的类（class）用 Class 描述

### springBean的注册到容器的方式
1. 手动注册的方式 `context.register(DemoConfig.class);`；不能操作过程
2. Scanner 扫描，自动注入的方式；不能操作过程
3. 实现 `ImportBeanDefinitionRegistrar` 接口；可以操作bean的实例化过程

