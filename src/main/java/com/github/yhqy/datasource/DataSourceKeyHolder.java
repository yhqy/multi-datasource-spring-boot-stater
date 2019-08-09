package com.github.yhqy.datasource;

class DataSourceKeyHolder {
    private final static ThreadLocal<String> dataSourceHolder = new ThreadLocal<>();

    static void set(String key) {
        dataSourceHolder.set(key);
    }

    static String get() {
        return dataSourceHolder.get();
    }

    static void clear() {
        dataSourceHolder.remove();
    }
}
