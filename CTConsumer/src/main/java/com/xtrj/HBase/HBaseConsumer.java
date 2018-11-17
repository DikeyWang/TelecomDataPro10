package com.xtrj.HBase;

import com.xtrj.Utils.PropertiesUtil;
import com.xtrj.dao.HBaseDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Collections;

public class HBaseConsumer {
    public static void main(String[] args) {
        //首先得到一个kafkaconsumer
        KafkaConsumer<String,String> kafkaConsumer = new KafkaConsumer<String, String>(PropertiesUtil.properties);
        //设置消费者topic
        kafkaConsumer.subscribe(Collections.singletonList(PropertiesUtil.getProperty("kafka.topics")));
        //实例一个hbdao
        //在dao里创建命名空间和表
        HBaseDao hBaseDao = new HBaseDao();
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord cr : records) {
                System.out.println(cr.value());
                hBaseDao.put(cr.value().toString());
            }
        }
    }

}

