package com.github.yhqy.datasource;

import cn.hutool.core.collection.ConcurrentHashSet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.Set;

/**
 * 动态数据源具体实现
 *
 * @author xchen
 * @date 2019/8/5 19:25
 */
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
