package com.xtrj.logproducer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 随机生成150个手机号
 * */
public class PhoneNumProducer {
    //存放手机号的List
    List<String> phoneNumList = new ArrayList<String>();
    //存放手机号随机的前两位
    String[] start = new String[]{"13","15","18"};

    /**
     * @return phoneNumList
     * 得到150个随机手机号
     * */
    public List<String> getPhoneNumList(){
        //生成手机号后9位
        String randomNum = "";
        String phoneNum = null;
        for (int i = 0;i < 3;i++){
            for (int j = 0;j < 50; j++){
                randomNum = new DecimalFormat("000000000").format(Math.random()*1000000000);
                phoneNum = start[i]+randomNum;
                //将得到的手机号存入List
                phoneNumList.add(phoneNum);
            }
        }
        return phoneNumList;
    }
}
