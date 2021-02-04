package com.pgk.chat.Controller;


import com.alibaba.fastjson.JSONObject;
import com.pgk.chat.Pojo.Test;
import com.pgk.chat.Pojo.Test;
import com.pgk.chat.Service.testService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class testController {
    @Autowired
    private testService service;

    @RequestMapping("/updatePic")
    public int updataPic(){
        return 1;
    }
    @RequestMapping("/downloadPic")
    public int downLoadPic(){
        return 1;
    }
    @RequestMapping("/downloadExcel")
    public int downLoadForm(@RequestBody List<Test> t1){
        for (Test test : t1) {
            System.out.print(test.getAddress());
            System.out.print(test.getDate());
            System.out.println(test.getName());
        }
//        int a = service.
        return 1;
    }

}
