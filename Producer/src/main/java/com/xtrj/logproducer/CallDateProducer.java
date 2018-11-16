package com.xtrj.logproducer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CallDateProducer {

    /**
     * 获取从startDate到endDate中间某个时间的时间戳
     * */
    public Calendar getCallDate(String startDate,String endDate){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            //将传入的startDate及endDate转为date，便于运算
            Date start = simpleDateFormat.parse(startDate);
            Date end = simpleDateFormat.parse(endDate);
            //结束时间不可小于开始时间
            if(start.getTime()>end.getTime()) return null;
            //得到一个随机的时间戳
            long resultTime = start.getTime() + (long)(Math.random()*(end.getTime()-start.getTime()));
            //活的一个calendar实例
            Calendar calendar = Calendar.getInstance();
            //将时间戳写入calendar实例
            calendar.setTimeInMillis(resultTime);
            return calendar;
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }
}
