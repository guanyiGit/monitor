package com.soholy.cb.service.activemq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.soholy.cb.common.ApplicationContextProvider;
import com.soholy.cb.entity.TDevice;
import com.soholy.cb.entity.TDeviceCommand;
import com.soholy.cb.entity.TDeviceDataWifi;
import com.soholy.cb.entity.TDeviceRecord;
import com.soholy.cb.entity.cdoec.CallBackData;
import com.soholy.cb.enums.CmdType;
import com.soholy.cb.service.TDeviceCommandService;
import com.soholy.cb.service.TDeviceDataWifiService;
import com.soholy.cb.service.TDeviceRecordService;
import com.soholy.cb.service.app.CmdService;
import com.soholy.cb.service.convert.WifiConvertService;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class AmqConsumer implements Runnable {

    private WifiConvertService wifiConvertService;
    private CallBackData input;
    private TDeviceDataWifiService tDeviceDataWifiService;
    private TDeviceRecordService tDeviceRecordService;
    private TDeviceCommandService tDeviceCommandService;
    private CmdService cmdService;

    public AmqConsumer(String message) {
        try {
            this.input = JSON.parseObject(message, CallBackData.class);

            this.tDeviceDataWifiService = ApplicationContextProvider.getBean(TDeviceDataWifiService.class);
            this.tDeviceRecordService = ApplicationContextProvider.getBean(TDeviceRecordService.class);
            this.tDeviceCommandService = ApplicationContextProvider.getBean(TDeviceCommandService.class);
            this.cmdService = ApplicationContextProvider.getBean(CmdService.class);
            this.wifiConvertService = ApplicationContextProvider.getBean(WifiConvertService.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (null == this.input || input.getDataType() == null) {
            return;
        }

        //wifi转gps
        this.wifiToGps();

        //保存数据
        this.saveData();

        //数据检验（禁养区域检测和低电量 告警）
        this.checkData();

        // 收到设数据，检查有没有要下发的命令进行下发 开机不下发命令
        this.scanCommand(input.gettDevice());

    }

    /**
     * wifi转gps
     */
    private void wifiToGps() {
        List<TDeviceDataWifi> wfs = this.input.gettDeviceDataWifis();
        if (wfs != null && wfs.size() > 0) {
            this.input.settDeviceDataWifis(
                    wfs.stream()
                            .filter(x -> x != null)
                            .filter(x -> x.getBssid() != null && x.getRssi() != null)
                            .map(x -> {
                                try {
                                    String bssid = this.formatBssid(x.getBssid());
                                    Float rssi = x.getRssi();
                                    //remark：0 没能转换  1 转换成功  2 转换成功，但是无结果
                                    WifiConvertService.ConvertResult conver = wifiConvertService.wifiConverToGps(bssid, rssi);
                                    Integer mark = conver.getMake();
                                    x.setMark(mark);//0未转换，1已成功转换，2转换失败
                                    if (mark == 1) {
                                        input.setLng(conver.getLongitude().doubleValue());
                                        input.setLat(conver.getLatitude().doubleValue());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return x;

                            })
                            .collect(Collectors.toCollection(ArrayList::new))
            );

        }

    }

    private String formatBssid(String bssid) {
        //原始：A80CCA0D270F
        if (StringUtils.isBlank(bssid) || bssid.trim().length() != 12) {
            return null;
        }
        bssid = bssid.trim();
        StringBuffer sb = new StringBuffer();
        //每2个一次，添加冒号
        for (int i = 0; i < bssid.length(); i += 2) {
            sb.append(bssid.substring(i, i + 2)).append(":");
        }
        bssid = sb.toString().substring(0, sb.toString().length() - 1);

        //例：A8:0C:CA:0D:27:0F
        if (bssid.length() != 17) {
            return null;
        }

        return bssid;
    }

    /**
     * 收到设数据，检查有没有要下发的命令进行下发 开机不下发命令
     *
     * @param tDevice
     */
    private void scanCommand(TDevice tDevice) {
        try {
            List<TDeviceCommand> cmds = tDeviceCommandService.findCmdByNoAndStatus(tDevice.getDeviceNumber(), 0);
            if (cmds != null) {
                cmds.stream()
                        .forEach(x -> {
                            //0 模式设置  1间隔时间设置
                            CmdType cmdType = x.getCmdType() == 0 ? CmdType.WORKPATTERN : CmdType.INTERVALTIME;
                            Integer cmdValue = x.getCmdValue();
                            Integer cmdMid = x.getCmdMid() == null ? cmdService.generateMid() : x.getCmdMid();
                            //命令发送
                            JSONObject cmdJson = null;
                            try {
                                cmdJson = cmdService.sendCommand(cmdType, cmdValue, cmdMid, tDevice.getDeviceIdIot());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String iotCommandId = null;
                            if (cmdJson != null) {
                                iotCommandId = cmdJson.getString("commandId");
                            }
                            if (cmdJson != null && StringUtils.isNoneBlank(cmdJson.getString("commandId"))) {
                                //修改命令状态
                                TDeviceCommand tdevCmd = new TDeviceCommand();
                                tdevCmd.setCmdStatus(1);
                                tdevCmd.setIotCommandId(iotCommandId);// 返回的结果和iotcmdid
                                tdevCmd.setDeviceCommandId(x.getDeviceCommandId());
                                tdevCmd.setCmdIssuedTime(LocalDateTime.now());

                                //修改命令状态
                                tDeviceCommandService.updateById(tdevCmd);

                            } else {// 命令下发成功 修改命令状态
                                log.warning("命令下发失败，命令id:" + x.getDeviceCommandId());
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据检验（禁养区域检测和低电量 告警）
     * 0 gps 1wifi 2 simple 3warn 4 start
     */
    private void checkData() {
        try {
            Integer dataType = input.getDataType();
            if (3 != dataType && dataType != 4) {
                //TODO
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存数据
     * 0 gps 1wifi 2 simple 3warn 4 start
     */
    private void saveData() {
        try {
            //wifi数据保存
            tDeviceDataWifiService.saves(input.gettDeviceDataWifis());

            TDevice device = input.gettDevice();
            //保存设备数据
            TDeviceRecord data = new TDeviceRecord();
            data.setCreateDate(LocalDateTime.now());
            data.setDataTime(input.getDataTime());
            data.setDeviceNo(device.getDeviceNumber());
            data.setQuantity(input.getQuantity());
            data.setUploadTime(input.getUploadTime());
            data.setId(input.getId());
            data.setTraceId(device.getTraceId());
            data.setIccid(input.getIccid());

            Integer dataType = input.getDataType();
            data.setDataType(dataType);
            if (dataType == 0 || dataType == 1) {//gps数据
                data.setLat(input.getLat());
                data.setLng(input.getLng());
            }

            //gps數據保存
            tDeviceRecordService.save(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
