package com.xtrj.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 该类主要用于根据用户传入的手机号以及开始和结束时间点，按月生成多组rowkey
 */
public class ScanRowkeyUtil {
    private String telephone;
    private String startDateString;
    private String stopDateString;
    List<String[]> list = null;

    int index = 0;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");

    public ScanRowkeyUtil(String telephone, String startDateString, String stopDateString) {
        this.telephone = telephone;
        this.startDateString = startDateString;
        this.stopDateString = stopDateString;
        list = new ArrayList<String[]>();
        genRowKeys();

    }

    //01_15837312345_201711
    //15837312345 2017-01-01 2017-05-01
    public void genRowKeys(){
        int regions = Integer.valueOf(MyPropertiesUtil.getProperty("hbase.regions.count"));
        try {
            Date startDate = sdf.parse(startDateString);
            Date stopDate = sdf.parse(stopDateString);

            //当前开始时间
            Calendar currentStartCalendar = Calendar.getInstance();
            currentStartCalendar.setTimeInMillis(startDate.getTime());
            //当前结束时间
            Calendar currentStopCalendar = Calendar.getInstance();
            currentStopCalendar.setTimeInMillis(startDate.getTime());
            //在当前开始时间的基础上加一个月
            currentStopCalendar.add(Calendar.MONTH, 1);

            while (currentStopCalendar.getTimeInMillis() <= stopDate.getTime()) {
                //反算出当前要查询的手机号的分区号
                String regionCode = HBaseUtil.genPartitionCode(telephone, sdf2.format(new Date(currentStartCalendar.getTimeInMillis())), regions);
                // 01_15837312345_201711
                String startRowKey = regionCode + "_" + telephone + "_" + sdf2.format(new Date(currentStartCalendar.getTimeInMillis()));
                String stopRowKey = regionCode + "_" + telephone + "_" + sdf2.format(new Date(currentStopCalendar.getTimeInMillis()));

                String[] rowkeys = {startRowKey, stopRowKey};
                list.add(rowkeys);
                currentStartCalendar.add(Calendar.MONTH, 1);
                currentStopCalendar.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断list集合中是否还有下一组rowkey
     * @return
     */
    public boolean hasNext() {
        if(index < list.size()){
            return true;
        }else{
            return false;
        }
    }

    /**
     *  取出list集合中存放的下一组rowkey
     * @return
     */
    public String[] next() {
        String[] rowkeys = list.get(index);
        index++;
        return rowkeys;
    }

}
