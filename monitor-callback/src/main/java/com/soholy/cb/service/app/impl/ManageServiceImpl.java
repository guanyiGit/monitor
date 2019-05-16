package com.soholy.cb.service.app.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soholy.cb.common.ConstantIot;
import com.soholy.cb.dao.TDeviceMapper;
import com.soholy.cb.entity.iot.deviceManager.CommandDTONA2Cloud;
import com.soholy.cb.entity.iot.deviceManager.UpdateDeviceInfoReqDTO;
import com.soholy.cb.service.app.AuthService;
import com.soholy.cb.service.app.ManageService;
import com.soholy.cb.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@PropertySource({"classpath:config/conf.properties"})
public class ManageServiceImpl implements ManageService {

    private static final Logger logger = LoggerFactory.getLogger(ManageServiceImpl.class);

    @Autowired
    private AuthService authService;

    @Value("${iot.codec.server.name}")
    private String serviceId;

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

    @Value("${iot.application.appid}")
    private String appid;

    @Value("${iot.application.secret}")
    private String secret;

    @Autowired
    private TDeviceMapper tDeviceMapper;

    @Override
    public JSONObject register(String nodeId, String endUserId, String psk, Integer timeout) throws Exception {
        if (StringUtils.isBlank(nodeId)) {
            logger.warn("register方法  参数 nodeId 为空");
            return null;
        }
        String url = authService.iotServerBaseUrl() + ConstantIot.REGISTER_DEVICE;

        Map<String, String> headers = authService.setAuthentication();

        JSONObject paramsJSON = new JSONObject();
        paramsJSON.put("nodeId", nodeId);

        paramsJSON.put("verifyCode", nodeId);

        if (StringUtils.isNotBlank(endUserId)) {
            paramsJSON.put("endUserId", endUserId);
        }
        if (StringUtils.isNotBlank(psk)) {
            paramsJSON.put("psk", psk);
        }
        if (timeout != null) {
            paramsJSON.put("timeout", timeout);
        }

        HttpClientUtil.HttpResult resp = HttpClientUtil.executePostParams(null, url, null, null, headers, paramsJSON.toJSONString());
        if (resp == null || resp.getStatusCode() != 200 || resp.getContent() == null) {
            logger.error("register error ,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(resp.getContent());
    }

    @Override
    public JSONObject register_gateway(String deviceId, String serviceId, CommandDTONA2Cloud message) throws Exception {
        String url = authService.iotServerBaseUrl() + ConstantIot.DISCOVER_INDIRECT_DEVICE;
        url = url.replace("{deviceId}", deviceId).replace("{serviceId}", serviceId);
        if (StringUtils.isBlank(deviceId) || StringUtils.isBlank(serviceId) || message == null
                || message.getHeader() == null) {
            logger.warn("register_gateway 方法参数有误");
            return null;
        }
        Map<String, String> headers = authService.setAuthentication();
        HttpClientUtil.HttpResult resp = HttpClientUtil.executePostParams(null, url, null, null, headers,
                JSONObject.toJSONString(message));
        if (resp == null || resp.getStatusCode() != 202 || resp.getContent() == null) {
            logger.error("register_gateway error ,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(resp.getContent());
    }

    public JSONObject findDeviceStatus(String deviceId) throws Exception {
        if (StringUtils.isBlank(deviceId)) {
            logger.warn("getDeviceStatus 方法参数有误");
            return null;
        }
        String url = authService.iotServerBaseUrl() + ConstantIot.REGISTER_DEVICE + "/{deviceId}".replace("{deviceId}", deviceId);
        Map<String, String> paramsObj = new HashMap<String, String>();
        paramsObj.put("appId", authService.iotServerBaseUrl() + this.appid);
        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "GET", null, authService.setAuthentication(), paramsObj,
                null);
        if (resp == null || resp.getStatusCode() != 200 || resp.getContent() == null) {
            logger.error("getDeviceStatus error,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(resp.getContent());
    }

    @Override
    public boolean deleteDevice(String deviceId) throws Exception {
        String url = authService.iotServerBaseUrl() + ConstantIot.DELETE_DEVICE + "/{deviceId}";
        url = url.replace("{deviceId}", deviceId).replace("{appId}", authService.iotServerBaseUrl() + this.appid).replace("{cascade}", "0");

        if (StringUtils.isBlank(deviceId)) {
            logger.warn("deleteDevice 方法参数有误");
            return false;
        }

        Map<String, String> headers = authService.setAuthentication();
        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "DELETE", null, headers, null, null);
        if (resp != null && resp.getStatusCode() == 204) {
            logger.error("deleteDevice error,result statusCode:" + resp.getStatusCode());
            return true;
        }
        return false;
    }

    @Override
    public JSONObject deleteDevice_gateway(CommandDTONA2Cloud message) throws Exception {
        String url = authService.iotServerBaseUrl() + ConstantIot.DISCOVER_INDIRECT_DEVICE;
        url = url/* .replace("{deviceId}", deviceId).replace("{serviceId}", serviceId) */.replace("{appId}",
                authService.iotServerBaseUrl() + this.appid);

        if (message == null || message.getHeader() == null) {
            logger.warn("deleteDevice_gateway 方法参数有误");
            return null;
        }

        Map<String, String> headers = authService.setAuthentication();

        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "POST", null, headers, JSONObject.toJSONString(message),
                null);
        if (resp == null || resp.getStatusCode() != 202) {
            logger.error("deleteDevice_gateway error,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(resp.getContent());
    }

    @Override
    public boolean modifyDeviceId(String deviceId, UpdateDeviceInfoReqDTO updateDeviceInfoReqDTO) throws Exception {
        if (deviceId == null) {
            logger.warn("modifyDeviceName 方法参数有误");
            return false;
        }
        String url = authService.iotServerBaseUrl() + ConstantIot.MODIFY_DEVICE_INFO + "/{deviceId}?appId={appId}";
        url = url.replace("{deviceId}", deviceId).replace("{appId}", authService.iotServerBaseUrl() + this.appid);

        Map<String, String> headers = authService.setAuthentication();
        String paramsObj = null;
        if (updateDeviceInfoReqDTO != null) {
            paramsObj = JSON.toJSONString(updateDeviceInfoReqDTO);
        }
        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "PUT", null, headers, paramsObj, null);
        if (resp != null && resp.getStatusCode() == 204) {
            return true;
        }
        logger.error("modifyDeviceName error,result statusCode:" + resp.getStatusCode());
        return false;
    }

    @Override
    public JSONObject freshDeviceAuth(String deviceId, String verifyCode, String nodeId, Integer timeout)
            throws Exception {
        if (deviceId == null) {
            logger.warn("freshDeviceAuth 方法参数有误");
            return null;
        }
        String url = authService.iotServerBaseUrl() + ConstantIot.REGISTER_DEVICE + "/{deviceId}?appId={appId}";
        url = url.replace("{deviceId}", deviceId).replace("{appId}", authService.iotServerBaseUrl() + this.appid);

        Map<String, String> headers = authService.setAuthentication();

        JSONObject jsonObject = new JSONObject();
        if (StringUtils.isNotBlank(verifyCode)) {
            jsonObject.put("verifyCode", verifyCode);
        }
        if (StringUtils.isNotBlank(nodeId)) {
            jsonObject.put("nodeId", nodeId);
        }
        if (timeout != null) {
            jsonObject.put("timeout", timeout);
        }

        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "PUT", null, headers, jsonObject.toJSONString(), null);

        if (resp == null || resp.getStatusCode() != 200) {
            logger.error("freshDeviceAuth error,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(resp.getContent());
    }

    @Override
    public JSONObject setEncryption(String deviceId, String serviceType, JSONObject jsonObject) throws Exception {
        if (deviceId == null || serviceType == null) {
            logger.warn("setEncryption 方法参数有误");
            return null;
        }
        String url = authService.iotServerBaseUrl() + ConstantIot.DELETE_DEVICE + "/{deviceId}/services/{serviceType}";
        url = url.replace("{deviceId}", deviceId).replace("{serviceType}", serviceType);

        Map<String, String> headers = authService.setAuthentication();

        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "PUT", null, headers, jsonObject, null);

        if (resp == null || resp.getStatusCode() != 200) {
            logger.error("setEncryption error,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(resp.getContent());
    }

    @Override
    public JSONObject findDeviceInfo(String deviceIdIot) throws Exception {
        if (StringUtils.isBlank(deviceIdIot)) {
            logger.warn("setEncryption 方法参数有误");
            return null;
        }
        String url = authService.iotServerBaseUrl() + ConstantIot.QUERY_DEVICES + "/{deviceId}";
        url = url.replace("{deviceId}", deviceIdIot);

        Map<String, String> headers = authService.setAuthentication();

        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "GET", null, headers, null, null);

        if (resp == null || resp.getStatusCode() != 200) {
            logger.error("setEncryption error,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(resp.getContent());
    }



}
