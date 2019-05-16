package com.soholy.cb.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.soholy.cb.entity.TDevice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceMapperTest {


    @Autowired
    private DataSource dataSource;

    @Autowired
    private TDeviceMapper deviceMapper;

    @Test
    public void testDataSource() throws SQLException {
        System.out.println(dataSource.getConnection());
    }

    @Test
    public void insert() {
        TDevice device = new TDevice();
        device.setSite("站点编号");
        device.setDeviceNumber("设备编号");
        device.setDeviceCode("设备厂商代码");
        device.setDeviceCalss(1);//设备种类
        device.setDeviceModel("设备型号");
        device.setState(1);
        device.setLongitude(0.0d);
        device.setLatitude(0.0d);
        device.setRegionId("区域id");
        device.setDeviceProductionNumber("设备生产序列号");
        device.setInstallUnit("安装单位");
        device.setInstaller("安装人");
        device.setUserId(1);
        device.setCreateTime(LocalDateTime.now());
        device.setDeviceName("设备名称");

        Integer count = deviceMapper.insert(device);

        System.out.println(count);
    }

    @Test
    public void select() {
        TDevice device = new TDevice();
        Map<String, Object> map = new HashMap<>();
        map.put("device_name", "设备名称");
        deviceMapper.selectByMap(map).forEach(System.out::println);
    }

    @Test
    public void select2() {
        Page page = new Page(); // 分页工具
        page.setCurrent(1); // 当前页
        page.setSize(10); // 页大小
//        IPage<TDevice> iPage = deviceMapper.selectPageVo(page, "设备名称");
//        System.out.println(iPage);
    }
}