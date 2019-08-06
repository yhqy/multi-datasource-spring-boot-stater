package com.github.yhqy.datasource;

import javax.sql.DataSource;

/**
 * @author xchen
 * @date 2019/8/5 19:33
 */
public interface DataSourceFactory {

    /**
     * 创建默认数据源
     */
    DataSource createDefaultDataSource();

    /**
     * 根据所给key返回datasource
     * <p>
     * 此处key和数据源一一对应
     */
    DataSource createDataSource(String key);

    /**
     * 标识key字段名称
     * 用于从bean,map,collection中获取判别dataSource的key
     *
     * @return
     */
    String key();

}
