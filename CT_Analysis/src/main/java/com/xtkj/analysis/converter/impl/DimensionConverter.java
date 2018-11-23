package com.xtkj.analysis.converter.impl;

import com.xtkj.Utils.JDBCUtil;
import com.xtkj.Utils.LRUCache;
import com.xtkj.analysis.converter.IConverter;
import com.xtkj.analysis.kv.base.BaseDimension;
import com.xtkj.analysis.kv.impl.ContactDimension;
import com.xtkj.analysis.kv.impl.DateDimension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 维度对象转维度id类
 * @author DikeyWang
 */
public class DimensionConverter implements IConverter{
    //日志记录工具类
    private static final Logger logger = LoggerFactory.getLogger(DimensionConverter.class);
    //每个线程保留一个自己的connection
    private ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();
    //创建一个缓存队列
    private LRUCache<String,Integer> lruCache = new LRUCache<>(3000);

    public DimensionConverter() {
        //close JVM ->close connect of sql
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("正在关闭MySQL连接");
            JDBCUtil.closeCon(connectionThreadLocal.get());
            logger.info("MySQL连接已关闭");
        }));
    }

    /**
     * 1、判断内存缓存中是否已经有该维度的id，如果存在则直接返回该id
     * 2、如果内存缓存中没有，则查询数据库中是否有该维度id，如果有，则查询出来，返回该id，并缓存到内存中。
     * 3、如果数据库中也没有该维度id，则直接插入一条新的维度信息，成功插入后，重新查询该维度，返回该id，并缓存到内存中。
     *
     * @param dimension
     * @return
     */
    @Override
    public int getDimensionId(BaseDimension dimension) throws IOException {
        //首先根据dimension获取对应的cacheKey
        String cacheKey = genCacheKey(dimension);
        //判断缓存中是否存在给cacheKey
        //存在就直接return cacheKey
        if (lruCache.containsKey(cacheKey)){
            return lruCache.get(cacheKey);
        }

        //若缓存中不存在该cacheKey，从MySQL中查询
        String[] sqls = null;
        if (dimension instanceof DateDimension){
            sqls = genDateDimensionSQL();
        }else if (dimension instanceof ContactDimension){
            sqls = genContactDimensionSQL();
        }else {
            throw new IOException("当前dimension不存在");
        }

        try {
            Connection connection = getConnection();
            int id = -1;
            synchronized (this){
                id = executeSQL(connection,sqls,dimension);
                lruCache.put(cacheKey,id);
                return id;
            }
        }catch (SQLException sqlE){
            sqlE.printStackTrace();
            throw new IOException(sqlE);
        }
    }

    /**
     * 生成dimension对应的cacheKey
     * LRUCACHE中缓存的键值对形式例如：<date_dimension20170820, 3>
     * @param dimension 传入的维度，根据此维度来生成cacheKey
     *                  有两种维度传入：DateDimension
     * @return String 生成的cacheKey
     * */
    private String genCacheKey(BaseDimension dimension){
        StringBuilder sb = new StringBuilder();
        if (dimension instanceof DateDimension){
            DateDimension dateDimension = (DateDimension) dimension;
            sb.append("date_dimension")
                    .append(dateDimension.getYear())
                    .append(dateDimension.getMonth())
                    .append(dateDimension.getDay());
        }else if (dimension instanceof ContactDimension){
            ContactDimension contactDimension = (ContactDimension) dimension;
            //本系统中，call==marking，本身marking应当是姓名，但是我懒得去调用随机姓名
            sb.append("contact_dimension")
                    .append(contactDimension.getCall())
                    .append(contactDimension.getMarking());
        }
        if (sb.length() <= 0) throw new RuntimeException("failed to create cachekey." + dimension);
        return  sb.toString();
    }

    /**
     * 若传入的是datedimension，生成对应的sql操作对应的表
     * */
    private String[] genDateDimensionSQL(){
        String query = "SELECT `id` FROM `tb_dimension_date` WHERE `year` = ? AND `month` = ? AND `day` = ? order by `id`;";
        String insert = "INSERT INTO `tb_dimension_date`(`year`, `month`, `day`) VALUES(?, ?, ?);";
        return new String[]{query, insert};
    }
    /**
     * 若传入的是contactDimension，生成对应的sql操作对应的表
     * */
    private String[] genContactDimensionSQL(){
        String query = "SELECT `id` FROM `tb_contacts` WHERE `call` = ? AND `marking` = ? order by `id`;";
        String insert = "INSERT INTO `tb_contacts`(`call`, `marking`) VALUES(?, ?);";
        return new String[]{query, insert};
    }

    /**
     * 获取一个连接
     * 先尝试从缓存中获取，若缓存中没有，创建一个连接并写入缓存
     *
     * */
    private Connection getConnection() throws SQLException{
        Connection connection = null;
        synchronized (this){
            //先从缓存中获取
            connection = connectionThreadLocal.get();
            //判断缓存中是否存在
            if (connection==null||connection.isClosed()||connection.isValid(3)){
                //不存在则获取conn并写入缓存
                connection = JDBCUtil.getCon();
                connectionThreadLocal.set(connection);
            }
        }
        return connection;
    }

    /**
     * 执行查询，返回ID。
     * 若当前数据不存在于数据库，则将当前数据写入数据库
     * @param connection
     * @param sqls      第一个为查询语句，第二个为插入语句
     * @param dimension
     * @return
     */
    private int executeSQL(Connection connection, String[] sqls,BaseDimension dimension) throws SQLException{
        PreparedStatement pst = null;
        ResultSet rst = null;
        try {
            pst = connection.prepareStatement(sqls[0]);
            setArguments(pst,dimension);
            rst = pst.executeQuery();
            //若数据库中存在，则查询后返回ID
            if (rst.next()){
                return rst.getInt(1);
            }
            //数据库中不存在该数据
            pst = connection.prepareStatement(sqls[1]);
            setArguments(pst,dimension);
            rst = pst.executeQuery();

            //直接调用自己，此时数据库中存在数据，直接返回ID
            executeSQL(connection,sqls,dimension);
        }finally {
            JDBCUtil.closeRst(rst);
            JDBCUtil.closePst(pst);
            JDBCUtil.closeCon(connection);
        }
        throw new RuntimeException("获取ID失败");
    }

    /**
     * 将查询条件写入pst
     * @param pst
     * @param dimension
     * */
    private void setArguments(PreparedStatement pst , BaseDimension dimension) throws SQLException{
        int i = 0;
        if (dimension instanceof DateDimension){
            DateDimension dateDimension = (DateDimension) dimension;
            pst.setInt(++i,dateDimension.getYear());
            pst.setInt(++i,dateDimension.getMonth());
            pst.setInt(++i,dateDimension.getDay());
        }else if (dimension instanceof ContactDimension){
            ContactDimension contactDimension = (ContactDimension) dimension;
            pst.setString(++i,contactDimension.getCall());
            pst.setString(++i,contactDimension.getMarking());
        }
    }

}
