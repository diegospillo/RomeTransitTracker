package service;

import java.util.HashMap;
import java.util.Map;

public record DataRow (Map<String, String> rowMap){

    public void put(String key, String value) {
        this.rowMap.put(key, value);
    }

    public String get(String key) {
        return this.rowMap.get(key);
    }

    public Map<String, String> getRowMap() {
        return new HashMap<>(this.rowMap);
    }
}