package com.atguigu.utils;

import com.atguigu.bean.TransientSink;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class TradeTrademarkCategoryUserSpuOrderBean {
    // 窗口起始时间
    String stt;
    // 窗口结束时间
    String edt;
    // 品牌 ID
    String trademarkId;
    // 品牌名称
    String trademarkName;
    // 一级品类 ID
    String category1Id;
    // 一级品类名称
    String category1Name;
    // 二级品类 ID
    String category2Id;
    // 二级品类名称
    String category2Name;
    // 三级品类 ID
    String category3Id;
    // 三级品类名称
    String category3Name;

    // sku_id
    @TransientSink
    String skuId;

    // 用户 ID
    String userId;
    // spu_id
    String spuId;
    // spu 名称
    String spuName;
    // 下单次数
    @Builder.Default //作用是保留用户自己所给定的默认值
            Long orderCount = 0L;
    // 下单金额
    Double orderAmount;
    // 时间戳
    Long ts;

    public static void main(String[] args) {
        TradeTrademarkCategoryUserSpuOrderBean orderBean = builder().category2Id("1001").build();

        System.out.println(orderBean);
    }
}
