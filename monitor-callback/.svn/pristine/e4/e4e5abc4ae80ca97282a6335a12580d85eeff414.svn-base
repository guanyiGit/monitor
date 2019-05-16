package com.soholy.cb.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:config/conf.properties"},
        ignoreResourceNotFound = false, encoding = "UTF-8")
@Data
public class IotPropertiesConfig {

    @Value("${write.log.path}")
    private String logPath;

    @Value("${iot.codec.param.name}")
    private String codecParamName;

    @Value("${iot.codec.server.name}")
    private String serviceId;

    @Value("${iot.codec.command.name}")
    private String commandName;

    @Value("${iot.codec.command.value}")
    private String commandValue;

    @Value("${iot.callback.host}")
    private String callbackHost;

    @Value("${iot.callback.port}")
    private String callbackPort;


    @Value("${iot.application.appid}")
    private String appid;

    @Value("${iot.application.secret}")
    private String secret;

    @Value("${iot.server.address.host}")
    private String iotServerHost;

    @Value("${iot.server.address.post}")
    private String iotServerPost;

    @Value("${iot.device.info.type}")
    private String deviceType;

    @Value("${iot.device.info.manufacturerId}")
    private String manufacturerId;
    @Value("${iot.device.info.model}")
    private String model;

    @Value("${iot.codec.command.name}")
    private String cmdName;

    @Value("${iot.codec.command.value}")
    private String cmdProp;


}
