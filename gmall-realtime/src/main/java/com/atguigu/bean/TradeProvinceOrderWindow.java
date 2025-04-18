package com.atguigu.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * TradeProvinceOrderWindow
 *
 * @author Star Zhang
 * @date 2022/4/27 11:12
 */
@Data
@AllArgsConstructor
@Builder
public class TradeProvinceOrderWindow {
    // 窗口起始时间
    String stt;

    // 窗口结束时间
    String edt;

    // 省份 ID
    String provinceId;

    // 省份名称
    @Builder.Default
    String provinceName = "";

    //加一个set集合,上面加一个注解，不计算数据计算
    @TransientSink
    Set<String> orderIdSet;

    // 累计下单次数
    Long orderCount;


    // 累计下单金额
    Double orderAmount;

    // 时间戳
    Long ts;
}