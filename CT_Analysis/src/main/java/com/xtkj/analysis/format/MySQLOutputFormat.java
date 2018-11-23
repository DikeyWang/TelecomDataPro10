package com.xtkj.analysis.format;

import com.xtkj.Utils.JDBCCacheBean;
import com.xtkj.Utils.JDBCUtil;
import com.xtkj.analysis.converter.impl.DimensionConverter;
import com.xtkj.analysis.kv.base.BaseDimension;
import com.xtkj.analysis.kv.base.BaseValue;
import com.xtkj.analysis.kv.impl.ComDimension;
import com.xtkj.analysis.kv.impl.CountDurationValue;
import com.xtkj.constants.Constants;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 自定义Outputformat，对接Mysql
 * */
public class MySQLOutputFormat extends OutputFormat<BaseDimension,BaseValue> {
    @Override
    public RecordWriter<BaseDimension, BaseValue> getRecordWriter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        Connection conn = null;
        try {
            //获取MySQL连接
            conn.setAutoCommit(false);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return new MysqlRecordWriter(conn);
    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) throws IOException, InterruptedException {
        // 校检输出
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        String name = taskAttemptContext.getConfiguration().get(FileOutputFormat.OUTDIR);
        Path output = name == null ? null : new Path(name);
        return new FileOutputCommitter(output, taskAttemptContext);

    }

    static class MysqlRecordWriter extends RecordWriter<BaseDimension,BaseValue>{
        private Connection connection = null;
        private DimensionConverter dimensionConverter = null;
        private PreparedStatement pst = null;
        private int batchNumber = 0;
        int count = 0;

        public MysqlRecordWriter(Connection connection) {
            this.connection = connection;
            this.batchNumber = Constants.JDBC_DEFAULT_BATCH_NUMBER;
            this.dimensionConverter = new DimensionConverter();
        }

        @Override
        public void write(BaseDimension key, BaseValue value) throws IOException, InterruptedException {
            try {
                // 统计当前PreparedStatement对象待提交的数据量
                String sql = "INSERT INTO `tb_call`(`id_date_contact`, `id_date_dimension`, `id_contact`, `call_sum`, `call_duration_sum`) VALUES(?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE `id_date_contact` = ? ;";
                if (pst == null) {
                    pst = connection.prepareStatement(sql);
                }
                // 本次sql
                int i = 0;
                ComDimension comDimension = (ComDimension) key;
                CountDurationValue countDurationValue = (CountDurationValue) value;

                int id_date_dimension = dimensionConverter.getDimensionId(comDimension.getDateDimension());
                int id_contact = dimensionConverter.getDimensionId(comDimension.getContactDimension());
                int call_sum = countDurationValue.getCallSum();
                int call_duration_sum = countDurationValue.getCallDurationSum();

                String id_date_contact = id_date_dimension + "_" + id_contact;

                pst.setString(++i, id_date_contact);
                pst.setInt(++i, id_date_dimension);
                pst.setInt(++i, id_contact);
                pst.setInt(++i, call_sum);
                pst.setInt(++i, call_duration_sum);

                pst.setString(++i, id_date_contact);
                pst.addBatch();
                //当前缓存了多少个sql语句等待批量执行，计数器
                count++;

                // 批量提交
                if (count >= this.batchNumber) {
                    pst.executeBatch(); // 批量提交
                    connection.commit(); // 连接提交
                    count = 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close(TaskAttemptContext context) throws IOException, InterruptedException {
            try {
                pst.executeBatch();
                this.connection.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                JDBCUtil.closePst(pst);
                JDBCUtil.closeCon(connection);
            }
        }

    }

}
