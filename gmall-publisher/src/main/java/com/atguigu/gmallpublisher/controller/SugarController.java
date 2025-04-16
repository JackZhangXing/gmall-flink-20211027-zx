package com.atguigu.gmallpublisher.controller;

import com.atguigu.gmallpublisher.mapper.UvMapper;
import com.atguigu.gmallpublisher.service.GmvService;
import com.atguigu.gmallpublisher.service.UvService;
import org.apache.commons.lang3.StringUtils;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SugarController
 *
 * @author Star Zhang
 * @date 2022/4/28 22:12
 */
//@Controller
@RestController  // = @Controller+@ResponseBody
@RequestMapping("/api/sugar")
public class SugarController {

    @Autowired
    private GmvService gmvService;

    @Autowired
    private UvService uvService;

    @RequestMapping("/test1")
//    @ResponseBody
    public String test1() {
        System.out.println("aaa");
//        return "index.html";
//        return "success";

        return "{\"id\":\"1001\",\"name\":\"zhangsan\"}";
    }

    @RequestMapping("/test2")
    public String test2(@RequestParam("date") String date) {
        System.out.println(date);
        return date;
    }

    //方法里面的参数如果有默认值，那前端接口传参时，可以不用带参数
    @RequestMapping("/test3")
    public String test3(@RequestParam("name") String name, @RequestParam(value = "age", defaultValue = "20") int age, @RequestParam(value = "address", defaultValue = "hubei") String address) {
        System.out.println("name:" + name + ",age=" + age + ",address=" + address);
        return "success";
    }

    @RequestMapping("/gmv")
    public String getGmv(@RequestParam(value = "date", defaultValue = "0") int date) {
        if (date == 0) {
            date = getToday();
        }

//        System.out.println(">>>"+date);
        //查询数据
        double gmv = gmvService.getGmv(date);

        return " {" +
                "          \"status\": 0, " +
                "          \"msg\": \"\", " +
                "          \"data\": " + gmv + " " +
                "        }";


    }

    @RequestMapping("/ch")
    public String getUvByCh(@RequestParam(value = "date", defaultValue = "0") int date) {
        if (date == 0) {
            date = getToday();
        }
        Map uvByCh = uvService.getUvByCh(date);
        Set chs = uvByCh.keySet();
        Collection uvs = uvByCh.values();

        //拼接JSON字符串并返回结果
        return " {" +
                "          \"status\": 0," +
                "          \"msg\": \"\"," +
                "          \"data\": {" +
                "            \"categories\": [" +
                StringUtils.join(chs, ',') +
                "            ]," +
                "            \"series\": [" +
                "              {" +
                "                \"name\": \"手机品牌\"," +
                "                \"data\": [" +
                StringUtils.join(uvs, ",") +
                "                ]" +
                "              }" +
                "            ]" +
                "          }" +
                "        }";
    }

    private int getToday() {
        long ts = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Integer.parseInt(sdf.format(ts));
    }
}
