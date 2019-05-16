package com.soholy.cb.service;


import com.soholy.cb.entity.TDevice;
import com.soholy.cb.entity.TDeviceCommand;
import com.soholy.cb.entity.cdoec.CallBackData;
import com.soholy.cb.entity.cdoec.DecodeRsp;

import java.util.List;

public interface TDeviceCommandService {

    /**
     * 查询命令
     *
     * @param deviceNo 设备no  为空查询所有设备
     * @param status   设备状态
     * @return
     */
    List<TDeviceCommand> findCmdByNoAndStatus(String deviceNo, Integer status);


    boolean updateById(TDeviceCommand tdevCmd);

    void resStart(CallBackData data, TDevice device);

    boolean cmdResHandle(DecodeRsp decodeRsp);
}
