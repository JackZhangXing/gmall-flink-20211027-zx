package com.atguigu.bean;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * CartAddUuBean
 *
 * @author Star Zhang
 * @date 2022/4/24 9:01
 */
@Data
@AllArgsConstructor
public class CartAddUuBean {
    // 窗口起始时间
    String stt;

    // 窗口闭合时间
    String edt;

    // 加购独立用户数
    Long cartAddUuCt;

    // 时间戳
    Long ts;
}
