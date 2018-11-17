package com.xtrj.Utils;


import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @deprecated 从recourse获取配置信息的工具类
 * @author dikey
 * @version 1.0
 * */
public class PropertiesUtil {
    public static Properties properties = null;
    static{
        try {
            // 加载配置属性
            InputStream inputStream = ClassLoader.getSystemResourceAsStream("kafka.properties");
            properties = new Properties();
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param key 需要获取的属性名
     * @return 配置内容,string
     * */
    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}
