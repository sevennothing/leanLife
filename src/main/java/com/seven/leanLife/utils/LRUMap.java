package com.seven.leanLife.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUMap extends LinkedHashMap {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > 10000;
        }
}
