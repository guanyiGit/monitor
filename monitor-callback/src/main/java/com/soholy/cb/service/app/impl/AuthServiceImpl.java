package com.soholy.cb.service.app.impl;


import com.alibaba.fastjson.JSONObject;
import com.soholy.cb.common.ConstantIot;
import com.soholy.cb.config.IotPropertiesConfig;
import com.soholy.cb.entity.iot.AccessToken;
import com.soholy.cb.service.app.AuthService;
import com.soholy.cb.utils.HttpClientUtil;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guanY
 * @version 1.0.0
 * @ClassName TokenServiceImpl
 * @Description (鉴权相关)
 * @Date 2018年7月26日 上午9:59:22
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


    @Autowired
    private IotPropertiesConfig conf;

    /**
     * 获取iot服务器地址url
     *
     * @return
     */
    @Override
    public String iotServerBaseUrl() {
        return "https://" + conf.getIotServerHost() + ":" + conf.getIotServerPost();
    }


    @Override
    public Map<String, String> setAuthentication() throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("app_key", conf.getAppid());
        headers.put("Authorization", "Bearer " + this.getAccessToken().getAccessToken());
        headers.put("Content-Type", ContentType.APPLICATION_JSON.getMimeType());
        return headers;
    }

    public AccessToken getAccessToken() throws Exception {
        String url = this.iotServerBaseUrl() + ConstantIot.APP_AUTH;
        Map<String, String> paramsObj = new HashMap<String, String>();
        paramsObj.put("appId", conf.getAppid());
        paramsObj.put("secret", conf.getSecret());
        HttpClientUtil.HttpResult resp = HttpClientUtil.executePostParams(null, url, null, null, null, paramsObj);
        String content = resp.getContent();
        if (resp == null || resp.getStatusCode() != 200 || resp.getContent() == null) {
            logger.error("getAccessToken error ,result statusCode:" + resp.getStatusCode());
            return null;
        }
        return JSONObject.parseObject(content, AccessToken.class);
    }

    @Override
    public AccessToken refreshToken() throws Exception {
        String url = this.iotServerBaseUrl() + ConstantIot.REFRESH_TOKEN;
        Map<String, String> paramsObj = new HashMap<String, String>();
        paramsObj.put("appId", conf.getAppid());
        paramsObj.put("secret", conf.getSecret());
        paramsObj.put("refreshToken", this.getAccessToken().getRefreshToken());

        HttpClientUtil.HttpResult resp = HttpClientUtil.executePostParams(null, url, null, null, null,
                JSONObject.toJSONString(paramsObj));
        if (resp == null || resp.getStatusCode() != 200 || resp.getContent() == null) {
            logger.error("getAccessToken error ,result statusCode:" + resp.getStatusCode());
            return null;
        }
        String content = resp.getContent();
        return JSONObject.parseObject(content, AccessToken.class);
    }

    public boolean logoutAuth() throws Exception {
        String url = this.iotServerBaseUrl() + ConstantIot.APP_AUTH_LOGOUT;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", this.getAccessToken());
        HttpClientUtil.HttpResult resp = HttpClientUtil.executeHttpParams(url, "POST", null, null, jsonObject.toJSONString(), null);
        if (resp == null || resp.getStatusCode() != 204) {
            logger.error("logoutAuth error ,result statusCode:" + resp.getStatusCode());
            return false;
        }
        return true;
    }

}
