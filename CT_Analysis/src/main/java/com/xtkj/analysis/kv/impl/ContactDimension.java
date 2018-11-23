package com.xtkj.analysis.kv.impl;

import com.xtkj.analysis.kv.base.BaseDimension;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.IllegalFormatFlagsException;

public class ContactDimension extends BaseDimension {
    private int id;
    private String call;
    private String marking;

    public ContactDimension() {
        super();
    }

    public ContactDimension(String call, String marking) {
        this.call = call;
        this.marking = marking;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getMarking() {
        return marking;
    }

    public void setMarking(String marking) {
        this.marking = marking;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        //why
        ContactDimension that = (ContactDimension) obj;
        if (this.id != that.id) return false;
        if (this.call != null ? !this.call.equals(that.call) : this.call != null) return false;
        return false;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31*result + (this.call != null ? this.call.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(BaseDimension o) {
        if (this==o) return 0;
        ContactDimension that = (ContactDimension) o;
        int tmp = Integer.compare(this.id,that.id);
        if (tmp!=0) return tmp;
        tmp = this.call.compareTo(that.call);
        if (tmp!=0) return tmp;
        return 0;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(this.id);
        dataOutput.writeUTF(this.call);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.id = dataInput.readInt();
        this.call = dataInput.readUTF();
    }

    @Override
    public String toString() {
        return "ContactDimension{" +
                "id=" + id +
                ", call='" + call + '\'' +
                '}';
    }
}
