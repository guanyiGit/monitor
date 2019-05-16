package com.soholy.cb.service.app;

import com.soholy.cb.config.IotPropertiesConfig;
import com.soholy.cb.enums.CallbackType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 初始化注册iot回调接口
 */
@Component
public class RegisterCallBack {

    @Autowired
    private IotPropertiesConfig conf;


    @Autowired
    private AppService appService;

    private Logger logger = LoggerFactory.getLogger(RegisterCallBack.class);

    //callbcak mapping base url
    private String mappingBaseUrl = "callback";

    /**
     * 获取回调服务器地址url
     *
     * @return
     */
    private String callbackUrl() {
        return "https://" + this.conf.getCallbackHost() + ":" + conf.getCallbackPort();
    }


    @PostConstruct
    public void register() {
        try {
            //数据批量上传
            String deviceDatasChanged = callbackUrl() + "/" + mappingBaseUrl + "/" + CallbackType.deviceDatasChanged;
            boolean bool1 = appService.subscribe(CallbackType.deviceDatasChanged, deviceDatasChanged);
            if (!bool1) {
                logger.warn("订阅上传callback失败");
            }
            logger.info("订阅上传callback");

            //设备绑定
            String bindDevice = callbackUrl() + "/" + mappingBaseUrl + "/" + CallbackType.bindDevice;
            boolean bool3 = appService.subscribe(CallbackType.bindDevice, bindDevice);
            if (!bool3) {
                logger.warn("订阅设备绑定callback失败");
            }
            logger.info("订阅设备绑定callback");

            //命令响应
            String commandRsp = callbackUrl() + "/" + mappingBaseUrl + "/" + CallbackType.commandRsp;
            boolean bool4 = appService.subscribe(CallbackType.commandRsp, commandRsp);
            if (!bool4) {
                logger.warn("订阅命令响应callback失败");
            }

            logger.info("订阅命令响应callback");
            //设备添加
            String deviceAdded = callbackUrl() + "/" + mappingBaseUrl + "/" + CallbackType.deviceAdded;
            boolean bool5 = appService.subscribe(CallbackType.deviceAdded, deviceAdded);
            if (!bool5) {
                logger.warn("订阅设备添加callback失败");
            }
            logger.info("订阅设备添加callback");

            //设备信息变化
            String devicewarnChanged = callbackUrl() + "/" + mappingBaseUrl + "/" + CallbackType.deviceInfoChanged;
            boolean bool6 = appService.subscribe(CallbackType.deviceInfoChanged, devicewarnChanged);
            if (!bool6) {
                logger.warn("订阅设备信息变化callback失败");
            }
            logger.info("订阅设备信息变化callback");

            //消息确认
            String messageConfirm = callbackUrl() + "/" + mappingBaseUrl + "/" + CallbackType.messageConfirm;
            boolean bool7 = appService.subscribe(CallbackType.messageConfirm, messageConfirm);
            if (!bool7) {
                logger.warn("订阅息确认callback失败");
            }
            logger.info("订阅息确认callback");
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("订阅息确认callback失败", e);
        }
    }

}
