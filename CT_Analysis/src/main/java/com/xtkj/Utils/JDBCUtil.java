package com.xtkj.Utils;

import java.sql.*;

public class JDBCUtil {
    //选定mysql驱动
    private static final String driver = "com.mysql.jdbc.Driver";
    //选定数据库
    private static final String url = "jdbc:mysql://myhadoop200:3306/db_telecom?useUnicode=true&characterEncoding=UTF-8";
    //user = root
    private static final String user = "root";
    //password = root
    private static final String password = "root";

    /**
     * 获取数据库连接
     * */
    public static Connection getCon() throws SQLException{
        try {
            Class.forName(driver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     *  关闭con
     * */
    public static void closeCon(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭pst
     * */
    public static void closePst(PreparedStatement pst) {
        try {
            if (pst!=null) {
                pst.close();
                pst=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭rst
     * */
    public static void closeRst(ResultSet rst) {
        try {
            if (rst!=null) {
                rst.close();
                rst=null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
