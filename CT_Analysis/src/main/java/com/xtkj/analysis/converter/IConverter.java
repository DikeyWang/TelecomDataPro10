package com.xtkj.analysis.converter;

import com.xtkj.analysis.kv.base.BaseDimension;

import java.io.IOException;

public interface IConverter {
    // 根据传入的dimension对象，获取数据库中对应该对象数据的id，如果不存在，则插入该数据再返回
    int getDimensionId(BaseDimension dimension) throws IOException;
}
