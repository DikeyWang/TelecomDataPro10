import com.xtrj.logproducer.CallDateProducer;
import com.xtrj.logproducer.LogProducer;
import com.xtrj.logproducer.LogWriter;
import com.xtrj.logproducer.PhoneNumProducer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Test {
    @org.junit.Test
    public void phoneProduceTest(){
        List<String> pl = new PhoneNumProducer().getPhoneNumList();
        System.out.println(pl);
    }

    @org.junit.Test
    public void justTest(){
        /*Calendar calendar = new CallDateProducer().getCallDate("2017-11-15","2018-11-12");
        System.out.println(calendar.getTime());*/
        /*
        System.out.println(new LogProducer().getLog());*/
        LogWriter logWriter = new LogWriter();
        logWriter.logWriter("E:\\test\\telData\\a.txt");
    }
}
