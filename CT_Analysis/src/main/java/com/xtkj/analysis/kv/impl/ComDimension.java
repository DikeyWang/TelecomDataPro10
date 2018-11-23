package com.xtkj.analysis.kv.impl;

import com.xtkj.analysis.kv.base.BaseDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * 时间维度+联系人维度组合
 */
public class ComDimension extends BaseDimension {
    private DateDimension dateDimension = new DateDimension();
    private ContactDimension contactDimension = new ContactDimension();

    public ComDimension() {
        super();
    }

    public ComDimension(DateDimension dateDimension, ContactDimension contactDimension) {
        this.dateDimension = dateDimension;
        this.contactDimension = contactDimension;
    }

    public DateDimension getDateDimension() {
        return dateDimension;
    }

    public void setDateDimension(DateDimension dateDimension) {
        this.dateDimension = dateDimension;
    }

    public ContactDimension getContactDimension() {
        return contactDimension;
    }

    public void setContactDimension(ContactDimension contactDimension) {
        this.contactDimension = contactDimension;
    }

    @Override
    public int hashCode() {
        int result = dateDimension!=null?dateDimension.hashCode():0;
        result = 31*result + (contactDimension!=null?contactDimension.hashCode():0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this==obj) return true;
        if (obj!=null||this.getClass()!=obj.getClass()) return false;
        ComDimension that = (ComDimension) obj;
        if (dateDimension!=null?!dateDimension.equals(that.dateDimension):that.dateDimension!=null) return false;
        if (contactDimension!=null?!contactDimension.equals(that.contactDimension):that.contactDimension!=null);
        return false;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this==o) return 0;
        ComDimension that = (ComDimension) o;
        int tmp = this.dateDimension.compareTo(that.dateDimension);
        if (tmp!=0) return tmp;
        tmp = this.contactDimension.compareTo(that.contactDimension);
        return tmp;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        //学到了
        this.dateDimension.write(dataOutput);
        this.contactDimension.write(dataOutput);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.dateDimension.readFields(dataInput);
        this.contactDimension.readFields(dataInput);
    }
}
