package com.xtkj.analysis.kv.impl;

import com.xtkj.analysis.kv.base.BaseValue;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 通话次数与时长的封装
 * */
public class CountDurationValue extends BaseValue {
    //某个维度通话次数总和
    private int callSum;
    //某个维度通话时间总和
    private int callDurationSum;

    public CountDurationValue() {
        super();
    }

    public CountDurationValue(int callSum, int callDurationSum) {
        this.callSum = callSum;
        this.callDurationSum = callDurationSum;
    }

    public int getCallSum() {
        return callSum;
    }

    public void setCallSum(int callSum) {
        this.callSum = callSum;
    }

    public int getCallDurationSum() {
        return callDurationSum;
    }

    public void setCallDurationSum(int callDurationSum) {
        this.callDurationSum = callDurationSum;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(callSum);
        dataOutput.writeInt(callDurationSum);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.callSum = dataInput.readInt();
        this.callDurationSum = dataInput.readInt();
    }
}
