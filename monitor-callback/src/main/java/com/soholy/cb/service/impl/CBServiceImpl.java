package com.soholy.cb.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.soholy.cb.config.IotPropertiesConfig;
import com.soholy.cb.service.CBService;
import com.soholy.cb.service.activemq.AmqProducer;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*

alter table t_device add column iccid varchar(30) comment '卡号';
alter table t_device add column device_Id_Iot varchar(50) comment '平台id';
alter table t_device add column status int comment '设备状态 0未激活 1已激活';
alter table t_device add column TraceId varchar(50) comment '溯源id';

CREATE TABLE IF NOT EXISTS `t_device_record` (
  `id` varchar(50) NOT NULL ,
  `lng` double(9,6) DEFAULT NULL,
  `lat` double(9,6) DEFAULT NULL,
  `quantity` double(6,2) DEFAULT NULL,
  `device_no` varchar(50) DEFAULT NULL,
  `create_date` datetime DEFAULT NULL,
  `data_time` datetime DEFAULT NULL,
  `upload_time` datetime DEFAULT NULL,
  `data_type` int(2) DEFAULT NULL COMMENT '0 gps 1wifi 2 simple 3warn 4 start',
  `trace_id` varchar(50) NOT NULL,
  `iccid` varchar(50) comment 'sm卡id',
  PRIMARY KEY (`id`),
  KEY `index_device_no_r` (`device_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `t_device_command` (
  `device_command_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `device_no` bigint(50) NOT NULL,
  `cmd_type` int(11) NOT NULL COMMENT '0模式设置，1间隔时间设置',
  `cmd_value` int(150) DEFAULT '0' COMMENT '-1正常,-2省电，其他为实际设置',
  `operator_id` bigint(20) NOT NULL,
  `cmd_issued_time` datetime DEFAULT NULL,
  `cmd_mid` int(11) DEFAULT NULL,
  `cmd_status` int(255) NOT NULL DEFAULT '0' COMMENT '0待发送，1平台已下发，2iot已下发，3命令已送达，4发送失败，5成功响应，6失败响应',
  `cmd_rsp_time` datetime DEFAULT NULL,
  `creation_time` datetime NOT NULL,
  `iot_command_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '0',
  PRIMARY KEY (`device_command_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `t_device_data_wifi` (
  `device_data_wifi_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `bssid` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '0',
  `rssi` float DEFAULT '0',
  `device_data_id` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `device_no` bigint(20) NOT NULL,
  `creation_time` datetime NOT NULL,
  `mark` int NOT NULL DEFAULT '0' COMMENT '0未转换，1已成功转换，2转换失败',
  PRIMARY KEY (`device_data_wifi_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

 */
@Service
@Log
public class CBServiceImpl implements CBService {

    private static ExecutorService THREADPOOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() <= 4 ? Runtime.getRuntime().availableProcessors() : 4);


    @Autowired
    private IotPropertiesConfig conf;

    @Override
    public boolean deviceDatasChanged(JSONObject json) {
        //数据初步处理
        THREADPOOL.execute(new AmqProducer(json, conf.getCodecParamName()));
        return true;
    }


}
