package com.weavict.edu.app

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.util.StringUtils

import javax.sql.DataSource

@Configuration
class DataSourceConfig
{
    @Primary
    @Bean(name = "appDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource")
    DataSourceProperties appDataSourceProperties()
    {
        return new DataSourceProperties();
    }

    @Primary
    @Bean(name = "appDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.hikari")
    DataSource appDataSource()
    {
        DataSourceProperties properties = this.appDataSourceProperties();
        return createHikariDataSource(properties);
    }


    @Bean(name = "camundaDataSourceProperties")
    @ConfigurationProperties(prefix = "spring.datasource.camunda")
    DataSourceProperties camundaDataSourceProperties()
    {
        return new DataSourceProperties();
    }

    @Bean(name = "camundaBpmDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.camunda.hikari")
    DataSource camundaDataSource() {
        // 获得 DataSourceProperties 对象
        DataSourceProperties properties = this.camundaDataSourceProperties();
        // 创建 HikariDataSource 对象
        return createHikariDataSource(properties);
    }

    static HikariDataSource createHikariDataSource(DataSourceProperties properties)
    {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
        if (StringUtils.hasText(properties.getName()))
        {
            dataSource.setPoolName(properties.getName());
        }
        return dataSource;
    }
}
