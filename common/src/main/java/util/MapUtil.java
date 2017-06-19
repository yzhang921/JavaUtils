package util;

import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    
    // default delimiter
    private static String elementDelimiter = "&";
    private static String mapkeyDelimiter = "=";
    
    public static <K, V> V getOrElse(Map<K, V> map, K key, V defV) {
        if (map == null) return null;
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return defV;
        }
    }
    
    public static <K, V> V get(Map<K, V> map, K key) {
        if (map == null) return null;
        if (map.containsKey(key)) {
            return map.get(key);
        } else {
            return null;
        }
    }
    
    public static Map<String, String> string2Map(String objStr) {
        return string2Map(objStr, elementDelimiter, mapkeyDelimiter);
    }
    
    public static Map<String, String> string2Map(String objStr, String elementDelimiter, String mapkeyDelimiter) {
        
        if (objStr == null)
            return null;
        
        Map<String, String> resultMap = new HashMap<String, String>();
        
        String[] elements = objStr.split(elementDelimiter);
        
        for (int i = 0; i < elements.length; i++) {
            String[] kvs = elements[i].split(mapkeyDelimiter);
            if (kvs[0] != null) {
                resultMap.put(kvs[0], kvs.length == 1 ? null : kvs[1]);
            }
        }
        
        return resultMap;
    }
}
