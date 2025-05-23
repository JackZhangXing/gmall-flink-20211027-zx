package com.atguigu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * WaterSensor
 *
 * @author Star Zhang
 * @date 2022/4/21 20:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaterSensor {
    private String id;
    private Double vc;
    private Long ts;
}
