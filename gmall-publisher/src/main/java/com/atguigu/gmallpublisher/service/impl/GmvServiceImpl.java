package com.atguigu.gmallpublisher.service.impl;

import com.atguigu.gmallpublisher.mapper.GmvMappper;
import com.atguigu.gmallpublisher.service.GmvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * GmvServiceImpl
 *
 * @author Star Zhang
 * @date 2022/4/28 22:50
 */
@Service
public class GmvServiceImpl implements GmvService {

    @Autowired
    private GmvMappper gmvMappper;

    @Override
    public double getGmv(int date) {
        return gmvMappper.selectGmv(date);
    }
}
