package com.github.yhqy.datasource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author xchen
 * @date 2019/8/5 19:31
 */
@Configuration
@ConditionalOnBean(DataSourceFactory.class)
@Import(DataSourceComponentRegister.class)
public class DataSourceAutoConfig {

    @Bean
    public DataSource dynamicDataSource(DataSourceFactory dataSourceFactory) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        DataSource defaultDataSource = dataSourceFactory.createDefaultDataSource();
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        dynamicDataSource.setTargetDataSources(new ConcurrentHashMap<>());
        return dynamicDataSource;
    }

}
