package com.atguigu.app.func;

import com.alibaba.fastjson.JSONObject;

/**
 * DimJoinFunction
 *
 * @author Star Zhang
 * @date 2022/4/26 21:46
 */
public interface DimJoinFunction<T> {

    String getKey(T input);

    void join(T input, JSONObject dimInfo);
}
