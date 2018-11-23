package com.xtkj.Utils;

import java.sql.Connection;
import java.sql.SQLException;

public class JDBCCacheBean {
    private static Connection connection = null;
    public JDBCCacheBean() {}
    public static Connection getInstance() {
        try {
            if (connection==null||connection.isClosed()||connection.isValid(3)){
                connection = JDBCUtil.getCon();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }
}
