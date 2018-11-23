package com.xtkj.analysis.reducer;

import com.xtkj.analysis.kv.impl.ComDimension;
import com.xtkj.analysis.kv.impl.CountDurationValue;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class CountDurationReducer extends Reducer<ComDimension,Text,ComDimension,CountDurationValue>{
    @Override
    protected void reduce(ComDimension key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        int durationSum = 0;
        for (Text value : values){
            count++;
            durationSum += Integer.valueOf(value.toString());
        }
        CountDurationValue countDurationValue = new CountDurationValue(count,durationSum);
        context.write(key,countDurationValue);
    }
}
