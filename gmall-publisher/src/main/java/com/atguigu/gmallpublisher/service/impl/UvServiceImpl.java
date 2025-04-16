package com.atguigu.gmallpublisher.service.impl;

import com.atguigu.gmallpublisher.mapper.UvMapper;
import com.atguigu.gmallpublisher.service.UvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UvServiceImpl
 *
 * @author Star Zhang
 * @date 2022/4/29 9:11
 */
@Service
public class UvServiceImpl implements UvService {

    @Autowired
    private UvMapper uvMapper;

    @Override
    public Map getUvByCh(int date) {
        //创建HashMap用于存放结果数据
        HashMap<String, Long> resultMap = new HashMap<>();

        List<Map> mapList = uvMapper.selectUvByCh(date);

        for (Map map : mapList) {
            resultMap.put((String) map.get("ch"),(Long) map.get("uv"));
        }

        //返回结果
        return resultMap;
    }
}
