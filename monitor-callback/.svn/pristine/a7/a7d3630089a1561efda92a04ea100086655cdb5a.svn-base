package com.soholy.cb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soholy.cb.enums.CallbackType;
import com.soholy.cb.service.CBService;
import com.soholy.cb.utils.R;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
import javax.jms.Topic;

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
    private Queue queue;


    @Autowired
    private Topic topic;

    @RequestMapping("/test")
    public R test() {
        log.info("test run ...");
        return R.ok();
    }

    @Autowired
    private JmsMessagingTemplate jms;


    @RequestMapping("/topic")
    public String topic(String input) {
        jms.convertAndSend(topic, input);
        return "topic 发送成功";
    }

    @RequestMapping("/queue")
    public String queue(String input) {
        jms.convertAndSend(queue, input);

        jms.send(queue, new Message<String>() {
            @Override
            public String getPayload() {
                log.info("当前生产线程为 ：  " + Thread.currentThread().getName() + ", 生产消息： \" " + input + " \"");
                return input;
            }

            @Override
            public MessageHeaders getHeaders() {
                return null;
            }
        });
        return "queue 发送成功";
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
