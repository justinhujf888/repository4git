package com.weavict.edu.app

import jakarta.persistence.Query
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch

@Component
class JpaSqlLogInterceptor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(JpaSqlLogInterceptor.class);

    void log(String sql, Object[] args) {
        LOGGER.info("Executing SQL: {}", sql);
        LOGGER.info("Params: {}", Arrays.toString(args));
    }

    void logQuery(String sql, Object[] args) {
        LOGGER.info("Query: {}", sql);
        LOGGER.info("Params: {}", Arrays.toString(args));
    }

    void logTime(StopWatch stopWatch) {
        LOGGER.info("Execution Time: {} ms", stopWatch.getTotalTimeMillis());
    }
}


class JpaSqlLogQueryInterceptor// implements QueryInterceptor
{
    private final JpaSqlLogInterceptor sqlLogInterceptor;

    JpaSqlLogQueryInterceptor(JpaSqlLogInterceptor sqlLogInterceptor) {
        this.sqlLogInterceptor = sqlLogInterceptor;
    }

//    @Override
    void beforeQuery(Query query, StopWatch stopWatch) {
        String sql = query.unwrap(org.hibernate.query.Query.class).getQueryString();
        Object[] args = query.getParameters().toArray();
        sqlLogInterceptor.logQuery(sql, args);
    }

//    @Override
    void afterQuery(Query query, StopWatch stopWatch) {
        sqlLogInterceptor.logTime(stopWatch);
    }
}

//@Configuration
//@EnableJpaRepositories(basePackages = "com.weavict.edu")
class JpaConfig {

    private final JpaSqlLogInterceptor sqlLogInterceptor;

//    @Autowired
    JpaConfig(JpaSqlLogInterceptor sqlLogInterceptor) {
        this.sqlLogInterceptor = sqlLogInterceptor;
    }

    @Bean
    HibernatePropertiesCustomizer hibernatePropertiesCustomizer(JpaVendorAdapter jpaVendorAdapter) {
        return  {properties ->
            properties.put(org.hibernate.cfg.Environment.INTERCEPTOR, (query, stopWatch) -> {
                JpaSqlLogQueryInterceptor interceptor = new JpaSqlLogQueryInterceptor(sqlLogInterceptor);
                interceptor.beforeQuery(query, stopWatch);
            });
        };
    }
}