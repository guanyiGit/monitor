package com.soholy.cb.service.impl;

import com.soholy.cb.dao.TDeviceDataWifiMapper;
import com.soholy.cb.entity.TDeviceDataWifi;
import com.soholy.cb.service.TDeviceDataWifiService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log
@Service
public class TDeviceDataWifiServiceImpl implements TDeviceDataWifiService {

    @Autowired
    private TDeviceDataWifiMapper tDeviceDataWifiMapper;

    @Override
    public boolean saves(List<TDeviceDataWifi> tDeviceDataWifis) {
        if (tDeviceDataWifis != null && tDeviceDataWifis.size() > 0)
            return tDeviceDataWifis.size() == tDeviceDataWifiMapper.saves(tDeviceDataWifis);
        return false;
    }
}
