package com.soholy.cb.controller;

import com.soholy.cb.service.TDeviceIotService;
import com.soholy.cb.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/device")
public class ManageContaoller {

    @Autowired
    private TDeviceIotService tDeviceIotService;

    /**
     * http://localhost:9000/device/register/13385285115?deviceBrand=nm_test&deviceBatch=test
     * * @param imei 设备imei号
     *
     * @param deviceBrand 品牌信息 可没有
     * @param deviceBatch 批次信息 可没有
     * @return
     * @throws Exception
     * @imei
     * @Description (设备注册)
     */
    @RequestMapping(value = "/register/{imei}", method = RequestMethod.GET)
    public R register(@PathVariable(value = "imei") String imei,
                      String deviceBrand,
                      String deviceBatch
    ) throws Exception {

        if (StringUtils.isNotBlank(imei)) {
            return R.ok(tDeviceIotService.register(imei, deviceBrand, deviceBatch));
        }
        return R.ok();
    }


    /**
     * 设备删除 平台和数据库同删除
     *
     * @param deviceIotId
     * @return
     */
    @RequestMapping(value = "/logoutDevice/{deviceIotId}")
    public R logoutDevice(@PathVariable("deviceIotId") String deviceIotId) {
        try {
            tDeviceIotService.logout(deviceIotId);
            return R.ok();
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(e.getMessage());
        }
    }


}
