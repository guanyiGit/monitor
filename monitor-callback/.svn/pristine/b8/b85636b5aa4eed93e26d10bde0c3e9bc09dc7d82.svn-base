package com.soholy.cb.service.app;


import com.soholy.cb.entity.iot.AccessToken;

import java.util.Map;

public interface AuthService {

    /**
     * 获取iot服务器地址url
     *
     * @return
     */
    String iotServerBaseUrl();

    /**
     * @return
     * @throws Exception
     * @Description (获取AccessToken)
     */
    AccessToken getAccessToken() throws Exception;

    /**
     * @return
     * @throws Exception
     * @Description (刷新 token)
     */
    AccessToken refreshToken() throws Exception;

    /**
     * @return
     * @throws Exception
     * @Description (注销鉴权信息)
     */
    boolean logoutAuth() throws Exception;

    /**
     * @return
     * @throws Exception
     * @Description (设置请求头)
     */
    Map<String, String> setAuthentication() throws Exception;
}
