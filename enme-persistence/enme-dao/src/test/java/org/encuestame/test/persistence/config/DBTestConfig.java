/*
 *
 *  * Copyright 2015 encuestame
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *
 */

package org.encuestame.test.persistence.config;

import org.apache.commons.dbcp.BasicDataSource;
import org.encuestame.config.annotations.EnMeDevProperties;
import org.encuestame.config.startup.EnMePlaceHolderConfigurer;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;


/**
 * Created by jpicado on 19/09/15.
 */
@Configuration
@Import(EnMeDevProperties.class)
@ComponentScan({ "org.encuestame.persistence" })
/**
 * Database Test Config.
 * Spring database configuration using latest Hibernate configuration.
 */
public class DBTestConfig {


    /**
     *
     * @return
     */
    @Bean(name = "dataSource")
    public DataSource restDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(EnMePlaceHolderConfigurer.getProperty("datasource.classname"));
        dataSource.setUrl(EnMePlaceHolderConfigurer.getProperty("datasource.urldb"));
        dataSource.setUsername(EnMePlaceHolderConfigurer.getProperty("datasource.userbd"));
        dataSource.setPassword(EnMePlaceHolderConfigurer.getProperty("datasource.pass"));
        return dataSource;
    }

    /**
     *
     * @return
     */
    @Bean(name="sessionFactory")
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(restDataSource());
        sessionFactory.setPackagesToScan(new String[]{"org.encuestame.persistence.domain"});
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }


    @Bean(name = "jdbcDataSource")
    public BasicDataSource jdbcDataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(EnMePlaceHolderConfigurer.getProperty("datasource.classname"));
        dataSource.setUrl(EnMePlaceHolderConfigurer.getProperty("datasource.urldb"));
        dataSource.setUsername(EnMePlaceHolderConfigurer.getProperty("datasource.userbd"));
        dataSource.setPassword(EnMePlaceHolderConfigurer.getProperty("datasource.pass"));
        return dataSource;
    }

    @Bean
    @Qualifier(value = "jdbcDataSource")
    public JdbcTemplate jdbcTemplate(BasicDataSource dataSource) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    /**
     *
     *
     * @param sessionFactory
     * @return
     */
    @Bean
    @Autowired
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory(sessionFactory);

        return txManager;
    }

    /**
     *
     * @param sessionFactory
     * @return
     */
    @Bean
    @Autowired
    public HibernateTemplate hibernateTemplate(SessionFactory sessionFactory) {
        final HibernateTemplate hibernateTemplate = new HibernateTemplate(sessionFactory);
        hibernateTemplate.setCacheQueries(Boolean.TRUE);
        return hibernateTemplate;
    }

    /**
     *
     * @return
     */
    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }


    Properties hibernateProperties() {
        return new Properties() {
            {
                setProperty("hibernate.hbm2ddl.auto", EnMePlaceHolderConfigurer.getProperty("datasource.hbm2ddl.auto"));
                setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
                setProperty("hibernate.globally_quoted_identifiers", "true");
                setProperty("hibernate.cache.provider_class", "org.hibernate.cache.NoCacheProvider");
                setProperty("hibernate.search.default.directory_provider", "org.hibernate.search.store.impl.RAMDirectoryProvider");
                setProperty("hibernate.search.default.indexBase", "/tmp");
                setProperty("hibernate.search.lucene_version", "LUCENE_CURRENT");
            }
        };
    }
}
