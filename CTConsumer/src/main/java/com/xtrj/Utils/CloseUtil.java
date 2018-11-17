package com.xtrj.Utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * 自写的工具类
 * @author:DikeyWang
 * @date:2018.11.7
 */
public class CloseUtil {
    /**
     * 参数为任意个可关闭的io，
     * return一个flag，默认：false，若传入的io全部关闭，则flag = true，
     * 报错信息wei：关闭某io时error。
     */
    public static boolean CloseIO(Closeable... io) {
        boolean flag = false;
        try {
            for (Closeable tem : io) {
                tem.close();
            }
            flag = true;
        } catch (IOException e) {
            System.out.println("关闭某io时error");
            e.printStackTrace();
        }
        return flag;
    }
}
