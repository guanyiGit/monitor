package com.soholy.cb.service.app.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soholy.cb.common.ConstantIot;
import com.soholy.cb.config.IotPropertiesConfig;
import com.soholy.cb.entity.iot.deviceManager.CommandDTOV4;
import com.soholy.cb.enums.CmdType;
import com.soholy.cb.service.app.AuthService;
import com.soholy.cb.service.app.CmdService;
import com.soholy.cb.service.codec.CodecService;
import com.soholy.cb.service.log.LogService;
import com.soholy.cb.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class CmdServiceImpl implements CmdService {

    @Autowired
    private IotPropertiesConfig conf;

    @Autowired
    private AuthService authService;

    @Autowired
    private CodecService codecService;

    @Autowired
    private LogService logService;

    /**
     * 获取回调服务器地址url
     *
     * @return
     */
    private String callbackUrl() {
        return "https://" + conf.getCallbackHost() + ":" + conf.getCallbackPort();
    }

    /**
     * 生成命令的 mid
     *
     * @return
     */
    @Override
    public int generateMid() {
        return (int) (Math.random() * 99999);
    }

    @Override
    public JSONObject sendCommand(CmdType cmdType, Integer cmdValue, Integer mid, String deviceIotId) throws Exception {
        if (StringUtils.isNotBlank(deviceIotId)) {
            mid = mid == null ? this.generateMid() : mid;

            byte[] input = codecService.generateComanmd(cmdType, cmdValue, mid);
            String url = authService.iotServerBaseUrl() + ConstantIot.POST_ASYN_CMD;
            Map<String, String> headers = authService.setAuthentication();
            JSONObject root = new JSONObject();
            root.put("deviceId", deviceIotId);

            CommandDTOV4 dtov4 = new CommandDTOV4();
            dtov4.setServiceId(conf.getServiceId());
            dtov4.setMethod(conf.getCommandName());
            Map<String, Object> paras = new HashMap<>();
            paras.put(conf.getCommandValue(), input);
            dtov4.setParas(paras);
            root.put("command", dtov4);
            root.put("callbackUrl", this.callbackUrl() + "/callback/cmdrsp");

            HttpClientUtil.HttpResult result = HttpClientUtil.executeHttpParams(url, "POST", headers, root.toJSONString());
            if (result != null && result.getStatusCode() == 201) {
                JSONObject object = JSON.parseObject(result.getContent());
                JSONObject resultObj = new JSONObject();
                resultObj.put("commandId", object.getString("commandId"));
                resultObj.put("output", Arrays.toString(input));
                logService.printLog("cmd output:" + Arrays.toString(input));
                return resultObj;
//                return object.getString("commandId");
            }

        }
        return null;
    }

}
