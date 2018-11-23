package com.xtkj.analysis.mapper;

import com.xtkj.analysis.kv.impl.ComDimension;
import com.xtkj.analysis.kv.impl.ContactDimension;
import com.xtkj.analysis.kv.impl.DateDimension;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;

public class CountDurationMapper extends TableMapper<ComDimension, Text>{
    private  byte[] family = Bytes.toBytes("f1");
    //联系人+时间维度
    private ComDimension comDimension = new ComDimension();

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        //数据
        //get line data "00_13883686062_20181023112058_15410384086_1_1240"
        String rowKey = Bytes.toString(value.getRow());
        //spilt data
        String[] valueLine = rowKey.split("_");
        //只想拿到主叫数据
        if (valueLine[4].equals("0")) return;

        //接受数据
        String call1 = valueLine[1];
        String call2 = valueLine[3];
        String duration = valueLine[5];
        int year = Integer.parseInt(valueLine[2].substring(0,4));
        int month = Integer.parseInt(valueLine[2].substring(4,6));
        int day = Integer.parseInt(valueLine[2].substring(6,8));
        //创建对象，暂时意义不明
        DateDimension dateDimensionYear = new DateDimension(year,-1,-1);
        DateDimension dateDimensionMonth = new DateDimension(year,month,-1);
        DateDimension dateDimensionDay = new DateDimension(year,month,day);


        //第一个号码
        //第二个参数作为标记
        ContactDimension contactDimension1 = new ContactDimension(call1,call1);
        comDimension.setContactDimension(contactDimension1);
        //why?
        comDimension.setDateDimension(dateDimensionYear);
        context.write(comDimension,new Text(duration));
        comDimension.setDateDimension(dateDimensionMonth);
        context.write(comDimension,new Text(duration));
        comDimension.setDateDimension(dateDimensionDay);
        context.write(comDimension,new Text(duration));

        //第二个号码
        ContactDimension contactDimension2 = new ContactDimension(call2,call2);
        comDimension.setContactDimension(contactDimension2);
        comDimension.setDateDimension(dateDimensionYear);
        context.write(comDimension,new Text(duration));
        comDimension.setDateDimension(dateDimensionMonth);
        context.write(comDimension,new Text(duration));
        comDimension.setDateDimension(dateDimensionDay);
        context.write(comDimension,new Text(duration));
    }
}
