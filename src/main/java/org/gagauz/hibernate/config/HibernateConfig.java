package org.gagauz.hibernate.config;

import org.gagauz.hibernate.dao.AbstractDao;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan(basePackageClasses = {AbstractDao.class})
public abstract class HibernateConfig {
    @Bean(autowire = Autowire.BY_NAME)
    public abstract DataSource dataSource();

    @Bean(autowire = Autowire.BY_NAME)
    public abstract LocalSessionFactoryBean sessionFactory();

    @Bean(autowire = Autowire.BY_NAME)
    public PlatformTransactionManager transactionManager() {
        HibernateTransactionManager tm = new HibernateTransactionManager();
        tm.setNestedTransactionAllowed(true);
        return tm;
    }

    @Bean(autowire = Autowire.BY_NAME)
    public TransactionInterceptor transactionInterceptor() {
        return new TransactionInterceptor();
    }

    @Bean
    public AnnotationTransactionAttributeSource transactionAttributeSource() {
        return new AnnotationTransactionAttributeSource();
    }

    @Bean(autowire = Autowire.BY_NAME)
    public TransactionAttributeSourceAdvisor transactionAttributeSourceAdvisor() {
        return new TransactionAttributeSourceAdvisor();
    }

}
