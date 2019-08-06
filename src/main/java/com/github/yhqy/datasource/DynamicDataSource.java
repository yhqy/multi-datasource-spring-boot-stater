package com.github.yhqy.datasource;

import cn.hutool.core.collection.ConcurrentHashSet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.Set;

public class DynamicDataSource extends AbstractConcurrentRoutingDataSource {

    private Set<String> hasCreateDataSource = new ConcurrentHashSet<>();

    @Autowired
    private DataSourceFactory dataSourceFactory;

    @Override
    protected Object determineCurrentLookupKey() {
        String key = DataSourceHolder.get();
        if (null == key) {
            return null;
        }
        if (hasCreateDataSource.contains(key)) {
            return key;
        }
        synchronized (this) {
            if (hasCreateDataSource.contains(key)) {
                return key;
            }
            DataSource dataSource = dataSourceFactory.createDataSource(key);
            addDataSource(key, dataSource);
            hasCreateDataSource.add(key);
            return key;
        }

    }
}
