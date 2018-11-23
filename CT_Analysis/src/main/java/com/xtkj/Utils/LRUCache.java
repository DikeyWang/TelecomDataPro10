package com.xtkj.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于缓存已知维度的id，减少对mysql的操作次数，提高效率
 * */
public class LRUCache<k,v> extends LinkedHashMap<k,v> {
    //why?
    private static final long serialVersionUID = -5907797767584803517L;
    //最大缓存数
    protected int maxElements;

    public LRUCache(int maxSize){
        super(maxSize,0.75f,true);
        this.maxElements = maxSize;
    }

    /**
     * @see java.util.LinkedHashMap#removeEldestEntry(java.util.Map.Entry)
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<k, v> eldest) {
        //Returns true if this map should remove its eldest entry.
        return (size()>this.maxElements);
    }
}
