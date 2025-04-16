package com.atguigu.app.func;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.atguigu.bean.TableProcess;
import com.atguigu.common.GmallConfig;
import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReadOnlyBroadcastState;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.util.Collector;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * TableProcessFunction
 *
 * @author Star Zhang
 * @description 描述该类的作用
 * @date 2022/4/16 8:21
 */
public class TableProcessFunction extends BroadcastProcessFunction<JSONObject, String, JSONObject> {
    private MapStateDescriptor<String, TableProcess> stateDescriptor;
    private Connection connection;

    @Override
    public void open(Configuration parameters) throws Exception {
        connection = DriverManager.getConnection(GmallConfig.PHOENIX_SERVER);
    }

    public TableProcessFunction(MapStateDescriptor<String, TableProcess> stateDescriptor) {
        this.stateDescriptor = stateDescriptor;
    }


    @Override
    public void processElement(JSONObject value, ReadOnlyContext ctx, Collector<JSONObject> out) throws Exception {
        //1.获取广播的配置数据
        ReadOnlyBroadcastState<String, TableProcess> broadcastState = ctx.getBroadcastState(stateDescriptor);
        TableProcess tableProcess = broadcastState.get(value.getString("table"));

        String type = value.getString("type");
        if (tableProcess != null && ("bootstrap-insert".equals(type) || "insert".equals(type) || "update".equals(type))) {

            //2.根据sinkColumns配置信息过滤字段
            filter(value.getJSONObject("data"), tableProcess.getSinkColumns());

            //3.补充sinkTable字段写出
            value.put("sinkTable", tableProcess.getSinkTable());
            out.collect(value);

        } else {
            System.out.println("过滤掉：" + value);
        }
    }

    @Override
    public void processBroadcastElement(String value, Context ctx, Collector<JSONObject> out) throws Exception {
        //1.获取并解析数据为JavaBean对象
        JSONObject jsonObject = JSON.parseObject(value);
        TableProcess tableProcess = JSON.parseObject(jsonObject.getString("after"), TableProcess.class);

        //2.校验表是否存在,如果不存在则建表
        checkTable(tableProcess.getSinkTable(),
                tableProcess.getSinkColumns(),
                tableProcess.getSinkPk(),
                tableProcess.getSinkExtend());

        //3.将数据写入状态
        String key = tableProcess.getSourceTable();
        BroadcastState<String, TableProcess> broadcastState = ctx.getBroadcastState(stateDescriptor);
        broadcastState.put(key, tableProcess);
    }


    //在Phoenix中校验并建表 create table if not exists db.tn(id varchar primary key,name varchar,....) xxx
    private void checkTable(String sinkTable, String sinkColumns, String sinkPk, String sinkExtend) {

        PreparedStatement preparedStatement = null;

        try {
            //处理字段
            if (sinkPk == null || sinkPk.equals("")) {
                sinkPk = "id";
            }
            if (sinkExtend == null) {
                sinkExtend = "";
            }

            StringBuilder sql = new StringBuilder("create table if not exists ")
                    .append(GmallConfig.HBASE_SCHEMA)
                    .append(".")
                    .append(sinkTable)
                    .append("(");

            String[] columns = sinkColumns.split(",");
            for (int i = 0; i < columns.length; i++) {

                //获取字段
                String column = columns[i];

                //判断是否为主键字段
                if (sinkPk.equals(column)) {
                    sql.append(column).append(" varchar primary key");
                } else {
                    sql.append(column).append(" varchar");
                }

                //不是最后一个字段,则添加","
                if (i < columns.length - 1) {
                    sql.append(",");
                }
            }

            sql.append(")").append(sinkExtend);

            System.out.println(sql);

            //预编译SQL
            preparedStatement = connection.prepareStatement(sql.toString());

            //执行
            preparedStatement.execute();

        } catch (SQLException e) {
            throw new RuntimeException("建表" + sinkTable + "失败！");
        } finally {
            //资源释放
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * @param data        {"id":"11","tm_name":"atguigu","logo_url":"/aaa/bbb"}
     * @param sinkColumns id,tm_name
     *                    <p>
     *                    {"id":"11","tm_name":"atguigu"}
     */
    private void filter(JSONObject data, String sinkColumns) {

        String[] split = sinkColumns.split(",");
        List<String> columnsList = Arrays.asList(split);

//        Iterator<Map.Entry<String, Object>> iterator = data.entrySet().iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, Object> next = iterator.next();
//            if (!columnsList.contains(next.getKey())) {
//                iterator.remove();
//            }
//        }

        data.entrySet().removeIf(next -> !columnsList.contains(next.getKey()));
    }
}
