package com.xtrj.dao;

import com.xtrj.Utils.HBaseUtil;
import com.xtrj.Utils.PropertiesUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class HBaseDao {
    private int regions;
    private String namespace;
    private String tableName;
    private String flag;
    private SimpleDateFormat simpleDateFormat;

    private static Configuration conf = null;
    private HTable callLogTable;

    static{
        //意义不明
        conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "myhadoop200,myhadoop201,myhadoop202");
        conf.set("hbase.zookeeper.master","myhadoop200:60000");
        conf.set("hbase.table.sanity.checks","false");
    }

    public HBaseDao() {
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        tableName = PropertiesUtil.getProperty("hbase.table.name");
        regions = Integer.valueOf(PropertiesUtil.getProperty("hbase.regions.count"));
        namespace = PropertiesUtil.getProperty("hbase.namespace");
        flag = PropertiesUtil.getProperty("hbase.caller.flag");

        if(!HBaseUtil.isExistTable(conf, tableName)){
            //已创建过命名空间
            /*HBaseUtil.initNamespace(conf, namespace);*/
            HBaseUtil.createTable(conf, tableName, "f1", "f2");
        }
    }
    /**
     * 15596505995,17519874292,2017-03-11 00:30:19,0652
     * 将当前数据put到HTable中
     * @param log
     */
    public void put(String log){
        try {
            callLogTable = new HTable(conf, tableName);

            String[] splits = log.split(",");

            String call1 = splits[0];
            String call2 = splits[1];
            String dateAndTime = splits[2];
            String timestamp = null;
            try {
                timestamp = String.valueOf(simpleDateFormat.parse(dateAndTime).getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String date = dateAndTime.split(" ")[0].replace("-", "");
            String time = dateAndTime.split(" ")[1].replace(":", "");
            String duration = splits[3];

            String regionHash = HBaseUtil.genPartitionCode(call1, date, regions);
            String rowKey = HBaseUtil.genRowKey(regionHash, call1, date + time, call2, flag, duration);
            Put put = new Put(Bytes.toBytes(rowKey));

            put.add(Bytes.toBytes("f1"), Bytes.toBytes("call1"), Bytes.toBytes(call1));
            put.add(Bytes.toBytes("f1"), Bytes.toBytes("call2"), Bytes.toBytes(call2));
            put.add(Bytes.toBytes("f1"), Bytes.toBytes("date_time"), Bytes.toBytes(date + time));
            put.add(Bytes.toBytes("f1"), Bytes.toBytes("date_time_ts"), Bytes.toBytes(timestamp));
            put.add(Bytes.toBytes("f1"), Bytes.toBytes("duration"), Bytes.toBytes(duration));
            put.add(Bytes.toBytes("f1"), Bytes.toBytes("flag"), Bytes.toBytes(flag));

            callLogTable.put(put);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
