package com.atguigu.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TableProcess
 * @author Star Zhang
 * @date 2022/4/16 8:21
 * 配置表实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableProcess {
    //来源表
    private String sourceTable;
    //输出表
    private String sinkTable;
    //输出字段
    private String sinkColumns;
    //主键字段
    private String sinkPk;
    //建表扩展
    private String sinkExtend;
}
