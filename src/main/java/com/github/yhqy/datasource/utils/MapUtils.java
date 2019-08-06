package com.github.yhqy.datasource.utils;

import cn.hutool.core.map.CaseInsensitiveMap;

import java.util.Map;

public class MapUtils {

    public static <String,V> V getIgnoreCase(Map<String,V> map, String key){
        Map<String,V> caseInsensitiveMap = new CaseInsensitiveMap(map);
        return caseInsensitiveMap.get(key);
    }

}
