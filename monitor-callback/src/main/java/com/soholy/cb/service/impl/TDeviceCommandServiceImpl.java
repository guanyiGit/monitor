package com.soholy.cb.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.soholy.cb.dao.TDeviceCommandMapper;
import com.soholy.cb.entity.TDevice;
import com.soholy.cb.entity.TDeviceCommand;
import com.soholy.cb.entity.cdoec.CallBackData;
import com.soholy.cb.entity.cdoec.DecodeRsp;
import com.soholy.cb.enums.CmdType;
import com.soholy.cb.enums.CodecVersion;
import com.soholy.cb.service.TDeviceCommandService;
import com.soholy.cb.service.app.CmdService;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Service
@Log
public class TDeviceCommandServiceImpl implements TDeviceCommandService {


    @Autowired
    private TDeviceCommandMapper tDeviceCommandMapper;


    @Autowired
    private CmdService cmdService;


    @Override
    public List<TDeviceCommand> findCmdByNoAndStatus(String deviceNo, Integer status) {

        LambdaQueryWrapper<TDeviceCommand> mrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotBlank(deviceNo)) {
            mrapper.eq(TDeviceCommand::getDeviceNo, deviceNo);
        }
        if (status != null) {
            mrapper.eq(TDeviceCommand::getCmdStatus, status);
        }
        return tDeviceCommandMapper.selectList(mrapper);
    }

    @Override
    public boolean updateById(TDeviceCommand tdevCmd) {
        return tDeviceCommandMapper.updateById(tdevCmd) == 1;
    }

    /**
     * 检测开机数据并且回复
     *
     * @param data
     * @param device
     */
    @Override
    public void resStart(CallBackData data, TDevice device, CodecVersion codecVersion) {
        if (data != null && data.getDataType() == 4) {
            int resultCode = 0;//默认未激活
            if (device != null && device.getStatus() != null && device.getStatus() == 1) {//设备已激活
                resultCode = 1;//已激活
            }
            //下发开机回复
            try {
                this.cmdService.sendCommand(CmdType.STARTING_UP, Integer.valueOf(resultCode), Integer.valueOf(this.cmdService.generateMid()), device.getDeviceIdIot(), codecVersion);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean cmdResHandle(DecodeRsp decodeRsp) {
        if (decodeRsp != null) {
            int resMid = decodeRsp.getMid();//命令响应后改变命令状态和时间

            TDeviceCommand command = new TDeviceCommand();

            Instant instant = decodeRsp.getRspTime().toInstant();
            command.setCmdRspTime(instant.atZone(ZoneId.systemDefault()).toLocalDateTime());
            String deviceNo = decodeRsp.getIMEI();
            //0x01成功，0x00失败。
            int code = 6;
            //0待发送，1平台已下发，2iot已下发，3命令已送达，4发送失败，5成功响应，6失败响应
            if (1 == decodeRsp.getResultCode()) {
                code = 5;
            }
            command.setCmdStatus(code);


            Integer cmdStatus = 0;
            return 1 == tDeviceCommandMapper.update(command, Wrappers.<TDeviceCommand>lambdaUpdate()
                    .eq(TDeviceCommand::getCmdMid, resMid)
                    .eq(TDeviceCommand::getDeviceNo, deviceNo)
                    .notIn(TDeviceCommand::getCmdStatus, cmdStatus));
        }
        return false;
    }

}
