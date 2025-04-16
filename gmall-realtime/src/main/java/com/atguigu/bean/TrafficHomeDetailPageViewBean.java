package com.atguigu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
/**
 * TrafficHomeDetailPageViewBean
 *
 * @author Star Zhang
 * @date 2022/4/23 10:19
 */


@Data
@AllArgsConstructor
public class TrafficHomeDetailPageViewBean {
    // 窗口起始时间
    String stt;

    // 窗口结束时间
    String edt;

    // 首页独立访客数
    Long homeUvCt;

    // 商品详情页独立访客数
    Long goodDetailUvCt;

    // 时间戳
    Long ts;
}