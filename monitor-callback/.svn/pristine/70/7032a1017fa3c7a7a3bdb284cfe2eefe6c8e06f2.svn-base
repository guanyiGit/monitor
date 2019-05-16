package com.soholy.cb.service.convert.impl;

import com.alibaba.fastjson.JSON;
import com.soholy.cb.common.DynamicDataSourceHolder;
import com.soholy.cb.dao.otherDb.WifiMapper;
import com.soholy.cb.entity.otherDb.WifiConvertGpsResult;
import com.soholy.cb.entity.otherDb.po.Wifi;
import com.soholy.cb.entity.otherDb.po.WifiExample;
import com.soholy.cb.service.convert.WifiConvertService;
import com.soholy.cb.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WifiConvertServiceImpl implements WifiConvertService {


    private static final Logger logger = LoggerFactory.getLogger(WifiConvertServiceImpl.class);

    @Autowired
    private WifiMapper wifiMapper;

    //WIFI热点位置数据
    // mac	  string	是	WIFI热点的MAC地址(BSSID) （00:87:36:05:5d:ea）
    // coord  string	否	坐标类型(wgs84/gcj02/bd09)，默认wgs84
    // output  string	否	返回格式(csv/json/xml)，默认csv

    @Value("${ConvertGps.url}")
    private String url;

    @Override
    public ConvertResult wifiConverToGps(String bssid, Float rssi) {
        ConvertResult rt = new ConvertResult();
        rt.setMake(2);
        if (StringUtils.isNotBlank(bssid)) {
            DynamicDataSourceHolder.setWifiDataSource();
            try {
                Wifi wifi = new Wifi();
                wifi.setBssid1(bssid);
                wifi.setRssi1(rssi);
                wifi.setPriId(UUID.randomUUID().toString());
                wifi.setUpdateTime(Calendar.getInstance().getTime());
                wifi.setCreationTime(Calendar.getInstance().getTime());

                //查
                ConvertResult cvrDb = this.findWifi(bssid);
                if (cvrDb != null) {
                    return cvrDb;
                }

                //没有查询调用接口 保存数据 再返回
                WifiConvertGpsResult wifiCvr = this.wifiConvertGps(bssid);
                if (wifiCvr != null) {
                    //1 成功  2 无结果
                    int make = wifiCvr != null && wifiCvr.getErrcode() == 0 ? 1 : 2;
                    wifi.setRemark(make);
                    wifi.setAddress(wifiCvr.getAddress());
                    wifi.setLatitude(wifiCvr.getLat());
                    wifi.setLongitude(wifiCvr.getLon());
                    wifi.setRssi1(wifiCvr.getRadius());

                    //存
                    this.saveWifi(wifi);
                }
            } finally {
                DynamicDataSourceHolder.setDefaultDataSouce();
            }
        }
        return rt;

    }

    private ConvertResult findWifi(String bssid) {
        if (StringUtils.isNotBlank(bssid)) {
            try {
                WifiExample example = new WifiExample();
                example.createCriteria().andBssid1EqualTo(bssid).andRemarkBetween(1, 2);//启用的,无结果的
                WifiExample.Criteria equal2 = example.createCriteria().andBssid2EqualTo(bssid).andRemarkBetween(1, 2);
                example.or(equal2);
                WifiExample.Criteria equal3 = example.createCriteria().andBssid3EqualTo(bssid).andRemarkBetween(1, 2);
                example.or(equal3);
                List<Wifi> wifis = wifiMapper.selectByExample(example);
                if (wifis != null && wifis.size() >= 1) {
                    Wifi wifi = wifis.get(0);
                    return new ConvertResult(wifi.getLongitude(), wifi.getLatitude(), wifi.getRemark());
                }
            } catch (Exception e) {
                logger.info("wifi数据查询失败", e);
            }
        }
        return null;
    }


    private boolean saveWifi(Wifi wifi) {
        if (wifi != null) {
            try {
                return wifiMapper.insertSelective(wifi) != 1;
            } catch (Exception e) {
                logger.warn("wifi数据入库失败", e);
            }
        }
        return false;
    }

    /**
     * wifi转gps
     *
     * @param bssid
     * @return
     */
    private WifiConvertGpsResult wifiConvertGps(String bssid) {
        //数据库没有调用接口查询
        //http://api.cellocation.com:81/wifi/?mac=00:87:36:05:5d:ea&coord=wgs84&output=json
        String tempUrl = (url + "/wifi/?mac=MAC&coord=wgs84&output=json").replace("MAC", bssid);
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("Accept", "text/plain;charset=utf-8");
            headers.put("Content-Type", "application/json; charset=utf-8");
            HttpClientUtil.HttpResult httpResult = HttpClientUtil.executeHttpParams(tempUrl, "get", null, headers, null, null);
            if (httpResult != null && httpResult.getStatusCode() == 200) {
                //{"errcode":0, "lat":"39.950027", "lon":"116.229858", "radius":"180", "address":"北京市海淀区四季青镇益园文创基地c区11号楼;南平庄中路与西平庄路路口西北574米"}
                WifiConvertGpsResult wifiConvertGpsResult = JSON.parseObject(httpResult.getContent(), WifiConvertGpsResult.class);
                return wifiConvertGpsResult;
            }
        } catch (Exception e) {
            logger.info("wifi坐标转换接口调用失败", e);
        }
        return null;
    }
}
