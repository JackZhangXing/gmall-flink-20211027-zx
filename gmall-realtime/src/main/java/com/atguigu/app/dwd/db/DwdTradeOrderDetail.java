package com.atguigu.app.dwd.db;

import com.atguigu.utils.MyKafkaUtil;
import com.atguigu.utils.MysqlUtil;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import java.time.Duration;

/**
 * DwdTradeOrderDetail
 *
 * @author Star Zhang
 * @description 描述该类的作用
 * @date 2022/4/19
 * 9.5 交易域订单事务事实表
 *
 * 需要先启动DwsTradeOrderWindow程序
 */
public class DwdTradeOrderDetail {
    public static void main(String[] args) throws Exception {
        //TODO 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env);

//        env.setStateBackend(new HashMapStateBackend());
//        env.enableCheckpointing(5000L);
//        env.getCheckpointConfig().setCheckpointTimeout(10000L);
//        env.getCheckpointConfig().setCheckpointStorage("hdfs:xxx:8020//xxx/xx");

        //设置存储状态时间
        tableEnv.getConfig().setIdleStateRetention(Duration.ofDays(3));

        //TODO 2.使用DDL读取kafka topic_db主题数据
        tableEnv.executeSql(MyKafkaUtil.getTopicDbDDL("dwd_trade_order_detail_211027"));


       //TODO 3.过滤出订单明细数据
        Table orderDetailTable = tableEnv.sqlQuery("" +
                "select " +
                "    data['id'] order_detail_id, " +
                "    data['order_id'] order_id, " +
                "    data['sku_id'] sku_id, " +
                "    data['sku_name'] sku_name, " +
                "    data['order_price'] order_price, " +
                "    data['sku_num'] sku_num, " +
                "    data['create_time'] create_time, " +
                "    data['source_type'] source_type, " +
                "    data['source_id'] source_id, " +
                "    cast(cast(data['sku_num'] as decimal(16,2)) * cast(data['order_price'] as decimal(16,2)) as String) split_original_amount, " +
                "    data['split_total_amount'] split_total_amount, " +
                "    data['split_activity_amount'] split_activity_amount, " +
                "    data['split_coupon_amount'] split_coupon_amount, " +
                "    pt " +
                "from topic_db " +
                "where `database`='gmall-211027-flink' " +
                "and `table`='order_detail' " +
                "and `type`='insert'");
        tableEnv.createTemporaryView("order_detail",orderDetailTable);


        //打印测试
        //Table table = tableEnv.sqlQuery("select * from order_detail");
        //tableEnv.toAppendStream(table, Row.class).print(">>>>>>>>>");

        //TODO 4.过滤出订单数据
        Table orderInfoTable = tableEnv.sqlQuery("" +
                "select " +
                "    data['id'] id, " +
                "    data['consignee'] consignee, " +
                "    data['consignee_tel'] consignee_tel, " +
                "    data['total_amount'] total_amount, " +
                "    data['order_status'] order_status, " +
                "    data['user_id'] user_id, " +
                "    data['payment_way'] payment_way, " +
                "    data['out_trade_no'] out_trade_no, " +
                "    data['trade_body'] trade_body, " +
                "    data['operate_time'] operate_time, " +
                "    data['expire_time'] expire_time, " +
                "    data['process_status'] process_status, " +
                "    data['tracking_no'] tracking_no, " +
                "    data['parent_order_id'] parent_order_id, " +
                "    data['province_id'] province_id, " +
                "    data['activity_reduce_amount'] activity_reduce_amount, " +
                "    data['coupon_reduce_amount'] coupon_reduce_amount, " +
                "    data['original_total_amount'] original_total_amount, " +
                "    data['feight_fee'] feight_fee, " +
                "    data['feight_fee_reduce'] feight_fee_reduce, " +
                "    `type`, " +
                "    `old` " +
                "from topic_db " +
                "where `database`='gmall-211027-flink' " +
                "and `table`='order_info' " +
                "and (`type`='insert' or `type`='update')");
        tableEnv.createTemporaryView("order_info", orderInfoTable);

        //TODO 5.过滤出订单明细活动数据
        Table orderActivityTable = tableEnv.sqlQuery("" +
                "select " +
                "    data['id'] id, " +
                "    data['order_id'] order_id, " +
                "    data['order_detail_id'] order_detail_id, " +
                "    data['activity_id'] activity_id, " +
                "    data['activity_rule_id'] activity_rule_id, " +
                "    data['sku_id'] sku_id, " +
                "    data['create_time'] create_time " +
                "from topic_db " +
                "where `database`='gmall-211027-flink' " +
                "and `table`='order_detail_activity' " +
                "and `type`='insert'");
        tableEnv.createTemporaryView("order_activity", orderActivityTable);

        //TODO 6.过滤出订单明细购物劵数据
        Table orderCouponTable = tableEnv.sqlQuery("" +
                "select " +
                "    data['id'] id, " +
                "    data['order_id'] order_id, " +
                "    data['order_detail_id'] order_detail_id, " +
                "    data['coupon_id'] coupon_id, " +
                "    data['coupon_use_id'] coupon_use_id, " +
                "    data['sku_id'] sku_id, " +
                "    data['create_time'] create_time " +
                "from topic_db " +
                "where `database`='gmall-211027-flink' " +
                "and `table`='order_detail_coupon' " +
                "and `type`='insert'");
        tableEnv.createTemporaryView("order_coupon", orderCouponTable);



        //TODO 7.构建mysql-lockup表  base_dic
        tableEnv.executeSql(MysqlUtil.getBaseDicLookUpDDL());

        //TODO 8.关联5张表
        Table resultTable = tableEnv.sqlQuery("" +
                "select " +
                "    od.order_detail_id, " +
                "    od.order_id, " +
                "    od.sku_id, " +
                "    od.sku_name, " +
                "    od.order_price, " +
                "    od.sku_num, " +
                "    od.create_time order_create_time, " +
                "    od.source_type, " +
                "    od.source_id, " +
                "    od.split_original_amount, " +
                "    od.split_total_amount, " +
                "    od.split_activity_amount, " +
                "    od.split_coupon_amount, " +
                "    od.pt, " +
                "    oi.consignee, " +
                "    oi.consignee_tel, " +
                "    oi.total_amount, " +
                "    oi.order_status, " +
                "    oi.user_id, " +
                "    oi.payment_way, " +
                "    oi.out_trade_no, " +
                "    oi.trade_body, " +
                "    oi.operate_time, " +
                "    oi.expire_time, " +
                "    oi.process_status, " +
                "    oi.tracking_no, " +
                "    oi.parent_order_id, " +
                "    oi.province_id, " +
                "    oi.activity_reduce_amount, " +
                "    oi.coupon_reduce_amount, " +
                "    oi.original_total_amount, " +
                "    oi.feight_fee, " +
                "    oi.feight_fee_reduce, " +
                "    oi.`type`, " +
                "    oi.`old`, " +
                "    oa.activity_id, " +
                "    oa.activity_rule_id, " +
                "    oa.create_time activity_create_time, " +
                "    oc.coupon_id, " +
                "    oc.coupon_use_id, " +
                "    oc.create_time coupon_create_time, " +
                "    dic.dic_name, " +
                "    current_row_timestamp() ts " +
                "from order_detail od " +
                "join order_info oi " +
                "on od.order_id = oi.id " +
                "left join order_activity oa " +
                "on od.order_detail_id = oa.order_detail_id " +
                "left join order_coupon oc " +
                "on od.order_detail_id = oc.order_detail_id " +
                "join base_dic FOR SYSTEM_TIME AS OF od.pt as dic " +
                "on od.source_type = dic.dic_code");
        tableEnv.createTemporaryView("result_table", resultTable);

        //TODO 9.构建kafka upsert-kafka表
        tableEnv.executeSql("" +
                "create table dwd_trade_order_detail_table( " +
                "    `order_detail_id` string, " +
                "    `order_id` string, " +
                "    `sku_id` string, " +
                "    `sku_name` string, " +
                "    `order_price` string, " +
                "    `sku_num` string, " +
                "    `order_create_time` string, " +
                "    `source_type` string, " +
                "    `source_id` string, " +
                "    `split_original_amount` string, " +
                "    `split_total_amount` string, " +
                "    `split_activity_amount` string, " +
                "    `split_coupon_amount` string, " +
                "    `pt` TIMESTAMP_LTZ(3), " +
                "    `consignee` string, " +
                "    `consignee_tel` string, " +
                "    `total_amount` string, " +
                "    `order_status` string, " +
                "    `user_id` string, " +
                "    `payment_way` string, " +
                "    `out_trade_no` string, " +
                "    `trade_body` string, " +
                "    `operate_time` string, " +
                "    `expire_time` string, " +
                "    `process_status` string, " +
                "    `tracking_no` string, " +
                "    `parent_order_id` string, " +
                "    `province_id` string, " +
                "    `activity_reduce_amount` string, " +
                "    `coupon_reduce_amount` string, " +
                "    `original_total_amount` string, " +
                "    `feight_fee` string, " +
                "    `feight_fee_reduce` string, " +
                "    `type` string, " +
                "    `old` map<string,string>, " +
                "    `activity_id` string, " +
                "    `activity_rule_id` string, " +
                "    `activity_create_time` string , " +
                "    `coupon_id` string, " +
                "    `coupon_use_id` string, " +
                "    `coupon_create_time` string , " +
                "    `dic_name` string, " +
                "    `ts` TIMESTAMP_LTZ(3), " +
                "    PRIMARY KEY (order_detail_id) NOT ENFORCED " +
                ")" + MyKafkaUtil.getUpsertKafkaDDL("dwd_trade_order_detail"));


        //TODO 10.将数据写出


        tableEnv.executeSql("insert into dwd_trade_order_detail_table select * from result_table").print();

        //TODO 11.启动任务
        env.execute("DwdTradeOrderDetail");


    }
}
