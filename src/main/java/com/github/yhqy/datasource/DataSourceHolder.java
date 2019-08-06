package com.github.yhqy.datasource;

/**
 * @author xchen
 * @date 2019/8/5 20:37
 */
class DataSourceHolder {
    private final static ThreadLocal<String> dataSourceHolder = new ThreadLocal<>();

    public static void set(String key) {
        dataSourceHolder.set(key);
    }

    public static String get() {
        return dataSourceHolder.get();
    }

    public static void clear() {
        dataSourceHolder.remove();
    }
}
