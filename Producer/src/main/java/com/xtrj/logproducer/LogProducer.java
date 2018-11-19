package com.xtrj.logproducer;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class LogProducer {

    List<String> phoneNumList = null;

    public LogProducer() {
        //获取随机的电话号码，150个
        phoneNumList = new PhoneNumProducer().getPhoneNumList();
    }

    public String getLog() {
        //获取建立通话的时间
        Calendar callTime = new CallDateProducer().getCallDate("2017-01-01","2018-11-15");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String callDate = simpleDateFormat.format(callTime.getTime());
        //获取主叫和被叫
        String call1 = phoneNumList.get(new Random().nextInt(phoneNumList.size()));
        String call2 = "";
        while (true){
            call2 = phoneNumList.get(new Random().nextInt(phoneNumList.size()));
            if (!call1.equals(call2)) break;
        }
        int duration = new Random().nextInt(30*60);
        String durationFormat = new DecimalFormat("0000").format(duration);

        StringBuilder logSB = new StringBuilder();
        logSB.append(call1+",").append(call2+",").append(callDate+",").append(durationFormat);
        try {
            Thread.sleep(300);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return logSB.toString();
    }
}
