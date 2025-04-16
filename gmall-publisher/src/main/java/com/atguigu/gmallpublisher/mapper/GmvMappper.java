package com.atguigu.gmallpublisher.mapper;

import org.apache.ibatis.annotations.Select;

/**
 * GmvMappper
 *
 * @author Star Zhang
 * @date 2022/4/28 22:39
 */
public interface GmvMappper {
    //查询ClickHouse，获取GMV总数
    //@Select("select sum(order_amount) from dws_trade_trademark_category_user_spu_order_window where toYYYYMMDD(stt)=#{date}")
    @Select("select sum(order_amount) from dws_trade_province_order_window where toYYYYMMDD(stt)=#{date}")
    double selectGmv(int date);
}
