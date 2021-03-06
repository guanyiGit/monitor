package com.soholy.cb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soholy.cb.enums.CallbackType;
import com.soholy.cb.service.CBService;
import com.soholy.cb.service.log.LogService;
import com.soholy.cb.utils.R;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author guanY
 * @version 1.0.0
 * @ClassName CBController
 * @Description (电信平台回调)
 * @Date 2018年4月12日 下午2:36:20
 */
/*
    通知类型，第三方应用可以根据通知类
    型接收物联网平台推送的对应通知消
    息。
    1. bindDevice（绑定设备，订阅后推送
    绑定设备通知）
    2. deviceAdded（添加新设备，订阅后
    推送注册设备通知）
    3. deviceInfoChanged（设备信息变化，
    订阅后推送设备信息变化通知）
    4. deviceDataChanged（设备数据变
    化，订阅后推送设备数据变化通
    知）
    5. deviceDatasChanged（设备数据批量
    变化，订阅后推送批量设备数据变
    化通知）
    6. deviceDeleted（删除设备，订阅后推
    送删除设备通知）
    7. serviceInfoChanged（服务信息变
    化，订阅后推送设备服务信息变化
    通知）
    8. ruleEvent（规则事件，订阅后推送规
    则事件通知）
    9. deviceModelAdded（添加设备模型，
    订阅后推送增加设备模型通知）
    10. deviceModelDeleted（删除设备模
    型，订阅后推送删除设备模型通
    知）
 */
@RestController
@RequestMapping(value = "/callback")
@Log
public class CBController {

    @Autowired
    private CBService cbService;

    @Autowired
    private LogService logService;

    @RequestMapping("/test")
    R test() {
        return R.ok("monitor");
    }


    @RequestMapping("/deviceDataChanged")
    public void deviceDataChanged(@RequestBody String body) {
        log.info("deviceDataChanged:" + body);
    }

    @RequestMapping("/deviceDatasChanged")
    public void deviceDatasChanged(@RequestBody String body) {
        log.info("deviceDatasChanged:" + body);
        try {
            JSONObject json = JSON.parseObject(body);
            String notifyType = json.getString("notifyType");
            if (CallbackType.deviceDatasChanged.toString().equals(notifyType)) {
                cbService.deviceDatasChanged(json);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.warning("电信凭条参数错误！");
        }
    }

    /**
     * http://192.168.0.69:18082/callback/datas
     * https://119.147.209.163:8082/callback/datas
     *
     * @param imei
     * @param pageNo
     * @param pageSize
     * @return
     */
    @RequestMapping({"/datas"})
    public R datas(@RequestParam(value = "imei", required = false) String imei, @RequestParam(value = "pageNo", required = false, defaultValue = "1") int pageNo, @RequestParam(value = "pageSize", required = false, defaultValue = "50") int pageSize) {
        try {
            return R.ok(this.logService.findLog(imei, pageNo, pageSize));
        } catch (Exception e) {
            e.printStackTrace();
            return R.error();
        }
    }

    @RequestMapping("/bindDevice")
    public void bindDevice(@RequestBody String body) {
        log.info("bindDevice:" + body);
    }

    @RequestMapping("/deviceAdded")
    public void deviceAdded(@RequestBody String body) {
        log.info("deviceAdded:" + body);
    }

    @RequestMapping("/deviceInfoChanged")
    public void deviceInfoChanged(@RequestBody String body) {
        log.info("deviceInfoChanged:" + body);
    }

    @RequestMapping("/deviceDeleted")
    public void deviceDeleted(@RequestBody String body) {
        log.info("deviceDeleted:" + body);
    }

    @RequestMapping("/serviceInfoChanged")
    public void serviceInfoChanged(@RequestBody String body) {
        log.info("serviceInfoChanged:" + body);
    }

    @RequestMapping("/ruleEvent")
    public void ruleEvent(@RequestBody String body) {
        log.info("ruleEvent:" + body);
    }

    @RequestMapping("/deviceModelAdded")
    public void deviceModelAdded(@RequestBody String body) {
        log.info("deviceModelAdded:" + body);
    }

    @RequestMapping("/deviceModelDeleted")
    public void deviceModelDeleted(@RequestBody String body) {
        log.info("deviceModelDeleted:" + body);
    }
}
