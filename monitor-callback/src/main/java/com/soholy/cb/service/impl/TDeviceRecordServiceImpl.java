package com.soholy.cb.service.impl;

import com.soholy.cb.dao.TDeviceRecordMapper;
import com.soholy.cb.entity.TDeviceRecord;
import com.soholy.cb.service.TDeviceRecordService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log
@Service
public class TDeviceRecordServiceImpl implements TDeviceRecordService {

    @Autowired
    private TDeviceRecordMapper tDeviceRecordMapper;

    @Override
    public boolean save(TDeviceRecord data) {
        if (data != null)
            return 1 == tDeviceRecordMapper.insert(data);
        return false;
    }
}
