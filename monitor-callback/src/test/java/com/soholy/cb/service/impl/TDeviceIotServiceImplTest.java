package com.soholy.cb.service.impl;

import com.soholy.cb.config.DruidConfig;
import com.soholy.cb.service.TDeviceIotService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import({DruidConfig.class})
public class TDeviceIotServiceImplTest {

    @Autowired
    private TDeviceIotService tDeviceIotService;

    @Test
    public void testModifyIccidById() {
        boolean b = tDeviceIotService.modifyIccidById(3, "111");
        System.out.println(b);
    }

}