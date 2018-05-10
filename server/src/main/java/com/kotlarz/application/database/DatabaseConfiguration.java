package com.kotlarz.application.database;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories( basePackages = "com.kotlarz.backend" )
@EnableTransactionManagement
public class DatabaseConfiguration
{
    @Bean( "h2datasource" )
    public DataSource getDataSource()
    {
        return DataSourceBuilder.create()
                        .url( "jdbc:h2:file:./database" )
                        .driverClassName( "org.h2.Driver" )
                        .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
                    @Qualifier( "h2datasource" ) DataSource dataSource )
    {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl( true );
        vendorAdapter.setDatabasePlatform( "H2" );

        LocalContainerEntityManagerFactoryBean factory =
                        new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter( vendorAdapter );
        factory.setPackagesToScan( "com.kotlarz.backend.domain" );
        factory.setDataSource( dataSource );
        return factory;
    }

    @Bean
    public PlatformTransactionManager transactionManager( EntityManagerFactory entityManager )
    {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory( entityManager );
        return manager;
    }
}
