package com.soholy.cb.service.codec.impl;


import com.soholy.cb.entity.cdoec.*;
import com.soholy.cb.enums.CmdType;
import com.soholy.cb.enums.CodecVersion;
import com.soholy.cb.service.codec.CodecService;
import com.soholy.cb.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CodecServiceImpl implements CodecService {

    public static final Logger logger = LoggerFactory.getLogger(CodecServiceImpl.class);
    public static final String CHARSET = "UTF-8";


    @Override
    public CodecBean decodec(byte[] inputBinary) {

        if (inputBinary == null || inputBinary.length < 2) {
            return null;
        }
        try {
            DecodeRsp decodeRsp = null;
            UploadBean uploadBean = null;
            int inputBinaryindex = 0;
            CodecVersion cdoe_version = CodecVersion.BASIC;
            String versionStr = String.valueOf(ByteUtils.byte1Toint(inputBinary, inputBinaryindex++));
            if (2 <= versionStr.length()) {
                versionStr = versionStr.substring(0, versionStr.length() - 2) + "." + versionStr.substring(versionStr.length() - 2, versionStr.length());
                float version = Float.valueOf(versionStr).floatValue();
                if (CodecVersion.CODEC_VERSION.getVersion() == version) {
                    cdoe_version = CodecVersion.CODEC_VERSION;
                } else if (CodecVersion.VOLTAGE.getVersion() == version) {
                    cdoe_version = CodecVersion.VOLTAGE;
                } else if (CodecVersion.ICCID.getVersion() == version) {
                    cdoe_version = CodecVersion.ICCID;
                } else
                    inputBinaryindex--;
            } else
                inputBinaryindex--;

            //固定1.5
            cdoe_version = CodecVersion.ICCID;

            byte code = inputBinary[inputBinaryindex++];// 请求码null
            if (0x01 == code) {// 定位数据上传(gps)
                uploadBean = this.decodeGpsUp(inputBinary, inputBinaryindex, cdoe_version);
                if (uploadBean == null) {
                    logger.warn("定位数据上传解析出错！");
                }
            } else if (0x02 == code) {// 设备告警
                uploadBean = this.decodeWarnUp(inputBinary, inputBinaryindex, cdoe_version);
                if (uploadBean == null) {
                    logger.warn("设备告警数据解码失败！");
                }
            } else if (0x04 == code) {// 设备数据(位置)上传间隔设置,响应
                decodeRsp = this.decodeSetIntervalTimeReq(inputBinary, inputBinaryindex);
                if (decodeRsp == null) {
                    logger.info("设备响应【间隔时间设备信息】解码错误！");
                } else {
                    logger.warn("设备数据(位置)上传间隔设置,响应解码失败！");
                }
            } else if (0x05 == code) {// 上传心跳数据(省电模式)
                uploadBean = this.decodeSimpleUp(inputBinary, inputBinaryindex, cdoe_version);
                if (uploadBean == null) {
                    logger.warn("设备心跳数据解码失败！");
                }
            } else if (0x07 == code) {// 设置设备工作模式 ,响应
                decodeRsp = this.decodeWorkPatternRsp(inputBinary, inputBinaryindex);
                if (decodeRsp == null) {
                    logger.warn("设置设备工作模式,设备响应【间隔时间设备信息】解码失败！");
                } else {
                    logger.warn("设置设备工作模式 ,响应解码失败！");
                }
            } else if (0x08 == code) {// 开机数据
                uploadBean = this.decodeStartUpData(inputBinary, inputBinaryindex, cdoe_version);
                if (uploadBean == null) {
                    logger.warn("开机数据解码失败！");
                }

            } else if (0x0A == code) {// 定位数据上传(wifi)
                uploadBean = this.decodeWifi(inputBinary, inputBinaryindex, cdoe_version);
                if (uploadBean == null) {
                    logger.warn("定位数据上传(wifi)解码失败！");
                }
            }
            CodecBean codecBean = new CodecBean();
            codecBean.setDecodeRsp(decodeRsp);
            codecBean.setUploadBean(uploadBean);
            return codecBean;
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("decode  err ", e);
        }
        return null;
    }

    /**
     * @param inputBinary      码流
     * @param inputBinaryindex 码流解析开始下标
     * @param version
     * @return
     * @throws Exception
     * @Description (设备开机数据解析 -)
     */
    private StartUpBean decodeStartUpData(byte[] inputBinary, int inputBinaryindex, CodecVersion version) {
        StartUpBean startUpBean = new StartUpBean();
        try {
            inputBinaryindex = codecBasic(inputBinary, inputBinaryindex, startUpBean, version);
            if (CodecVersion.ICCID.equals(version)) {
                byte[] ICCIDBytes = new byte[20];
                inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, ICCIDBytes.length, ICCIDBytes, 0)[0];
                startUpBean.setICCID(new String(ICCIDBytes, "UTF-8"));
            }
        } catch (Exception e) {
            logger.warn("设备开机请求数据解析失败！");
            e.printStackTrace();
            return null;
        }
        return startUpBean;
    }


    /**
     * @param inputBinary      码流
     * @param inputBinaryindex 码流解析开始下标
     * @return
     * @throws UnsupportedEncodingException
     * @Description (设置设备工作模式 - - 响应解析)
     */
    private DecodeRsp decodeWorkPatternRsp(byte[] inputBinary, int inputBinaryindex) {
        DecodeRsp decodeRsp = new DecodeRsp();
        try {
            byte[] midBytes = new byte[2];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, inputBinaryindex, midBytes.length, midBytes, 0)[0];
            ByteUtils.chckAndFillBytes(midBytes);
            int mid = ByteUtils.byte2Toint(midBytes, 0);
            byte[] IMEIBytes = new byte[8];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, IMEIBytes.length, IMEIBytes, 0)[0];
            ByteUtils.chckAndFillBytes(IMEIBytes);
            String imei = ByteUtils.byte2ToIntegerStr(IMEIBytes, 0, IMEIBytes.length);
            StringBuffer sb = new StringBuffer();
            char[] imeiChar = imei.toCharArray();
            for (int i = 1; i < imeiChar.length; i++)
                sb.append(imeiChar[i]);
            imei = sb.toString();
            byte[] respTimeBytes = new byte[4];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, respTimeBytes.length, respTimeBytes, 0)[0];
            ByteUtils.chckAndFillBytes(respTimeBytes);
            long byte4Tolong = ByteUtils.byte4Tolong(respTimeBytes, 0);
            byte[] resultBytes = new byte[1];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, resultBytes.length, resultBytes, 0)[0];
            ByteUtils.chckAndFillBytes(resultBytes);
            int resultCode = ByteUtils.byte1Toint(resultBytes, 0);
            decodeRsp.setIMEI(imei);
            decodeRsp.setMid(mid);
            decodeRsp.setResultCode(resultCode);
            decodeRsp.setRspTime(new Date(byte4Tolong * 1000L));
        } catch (Exception e) {
            logger.warn("设置设备工作模式 --响应解析错误！", e);
            return null;
        }
        return decodeRsp;
    }

    /**
     * @param inputBinary      码流
     * @param inputBinaryindex 码流解析开始下标
     * @return
     * @throws UnsupportedEncodingException
     * @Description (设置设备上传间隔设置, 设备的响应解码)
     */
    private DecodeRsp decodeSetIntervalTimeReq(byte[] inputBinary, int inputBinaryindex) {
        DecodeRsp decodeRsp = new DecodeRsp();
        try {
            byte[] midBytes = new byte[2];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, inputBinaryindex, midBytes.length, midBytes, 0)[0];
            ByteUtils.chckAndFillBytes(midBytes);
            int mid = ByteUtils.byte2Toint(midBytes, 0);
            byte[] IMEIBytes = new byte[8];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, IMEIBytes.length, IMEIBytes, 0)[0];
            ByteUtils.chckAndFillBytes(IMEIBytes);
            String imei = ByteUtils.byte2ToIntegerStr(IMEIBytes, 0, IMEIBytes.length);
            StringBuffer sb = new StringBuffer();
            char[] imeiChar = imei.toCharArray();
            for (int i = 1; i < imeiChar.length; i++)
                sb.append(imeiChar[i]);
            imei = sb.toString();
            byte[] respTimeBytes = new byte[4];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, respTimeBytes.length, respTimeBytes, 0)[0];
            ByteUtils.chckAndFillBytes(respTimeBytes);
            long rspTimeInt = ByteUtils.byte4Tolong(respTimeBytes, 0);
            byte[] resultBytes = new byte[1];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, resultBytes.length, resultBytes, 0)[0];
            ByteUtils.chckAndFillBytes(resultBytes);
            int result = ByteUtils.byte1Toint(resultBytes, 0);
            decodeRsp.setIMEI(imei);
            decodeRsp.setMid(mid);
            decodeRsp.setResultCode(result);
            decodeRsp.setRspTime(new Date(rspTimeInt * 1000L));
        } catch (Exception e) {
            logger.warn("设置设备上传间隔设置,设备的响应解码错误！", e);
            return null;
        }
        return decodeRsp;
    }

    /**
     * @param inputBinary
     * @param inputBinaryindex
     * @param version
     * @return
     * @throws UnsupportedEncodingException
     * @Description (解码wifi数据)
     */
    private WifiUpload decodeWifi(byte[] inputBinary, int inputBinaryindex, CodecVersion version) {
        WifiUpload uploadBean = new WifiUpload();
        try {
            inputBinaryindex = codecBasic(inputBinary, inputBinaryindex, uploadBean, version);
            byte[] lengthBytes = new byte[1];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, lengthBytes.length, lengthBytes, 0)[0];
            int length = ByteUtils.byte1Toint(lengthBytes, 0);
            List<WifiLocation> wifiLocations = new ArrayList<WifiLocation>();
            for (int i = 0; i < length; i++) {
                byte[] bssidBytes = new byte[6];
                inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, bssidBytes.length, bssidBytes, 0)[0];
                if (!ByteUtils.chckBytes(bssidBytes))
                    throw new UnsupportedEncodingException();
                String bssid = ByteUtils.byteTohex(bssidBytes);
                byte[] rssiBytes = new byte[1];
                inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, rssiBytes.length, rssiBytes, 0)[0];
                if (!ByteUtils.chckBytes(rssiBytes))
                    throw new UnsupportedEncodingException();
                float rssi = ByteUtils.byte1Toint(rssiBytes, 0);
                WifiLocation wifiLocation = new WifiLocation();
                wifiLocation.setBssid(bssid);
                wifiLocation.setRssi(rssi);
                wifiLocations.add(wifiLocation);
            }
            uploadBean.setLength(wifiLocations.size());
            uploadBean.setWifiLocation(wifiLocations);
            if (CodecVersion.ICCID.equals(version)) {
                byte[] ICCIDBytes = new byte[20];
                inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, ICCIDBytes.length, ICCIDBytes, 0)[0];
                uploadBean.setICCID(new String(ICCIDBytes, "UTF-8"));
            }
        } catch (Exception e) {
            logger.warn("解码wifi数据解码错误！", e);
            return null;
        }
        return uploadBean;
    }

    /**
     * @param inputBinary
     * @param inputBinaryindex
     * @param version
     * @return
     * @throws UnsupportedEncodingException
     * @Description (省电模式数据解析)
     */
    private SimpleUpload decodeSimpleUp(byte[] inputBinary, int inputBinaryindex, CodecVersion version) {
        SimpleUpload simpleUpload = new SimpleUpload();
        try {
            inputBinaryindex = codecBasic(inputBinary, inputBinaryindex, simpleUpload, version);
            if (CodecVersion.ICCID.equals(version)) {
                byte[] ICCIDBytes = new byte[20];
                inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, ICCIDBytes.length, ICCIDBytes, 0)[0];
                simpleUpload.setICCID(new String(ICCIDBytes, "UTF-8"));
            }
        } catch (Exception e) {
            logger.warn("设备开机请求数据解析失败！");
            e.printStackTrace();
            return null;
        }
        return simpleUpload;
    }

    /**
     * @param inputBinary
     * @param inputBinaryindex
     * @param version
     * @throws UnsupportedEncodingException
     * @Description (告警数据解析)
     */
    private WarnUpload decodeWarnUp(byte[] inputBinary, int inputBinaryindex, CodecVersion version) throws UnsupportedEncodingException {
        WarnUpload uploadBean = new WarnUpload();
        try {
            inputBinaryindex = codecBasic(inputBinary, inputBinaryindex, uploadBean, version);
            byte[] warnTimeBytes = new byte[4];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, warnTimeBytes.length, warnTimeBytes, 0)[0];
            ByteUtils.chckAndFillBytes(warnTimeBytes);
            long wanrTimeMi = ByteUtils.byte4Tolong(warnTimeBytes, 0);
            uploadBean.setWarnTime(new Date(wanrTimeMi * 1000L));
            if (CodecVersion.ICCID.equals(version)) {
                byte[] ICCIDBytes = new byte[20];
                inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, ICCIDBytes.length, ICCIDBytes, 0)[0];
                uploadBean.setICCID(new String(ICCIDBytes, "UTF-8"));
            }
        } catch (Exception e) {
            logger.warn("告警数据解析失败", e);
            e.printStackTrace();
            return null;
        }
        return uploadBean;
    }

    /**
     * @param inputBinary      数据源
     * @param inputBinaryindex 数据源下标
     * @param version
     * @throws UnsupportedEncodingException
     * @Description (gps数据解码)
     */
    private GpsUpload decodeGpsUp(byte[] inputBinary, int inputBinaryindex, CodecVersion version) {
        GpsUpload bean = new GpsUpload();
        try {
            inputBinaryindex = codecBasic(inputBinary, inputBinaryindex, bean, version);
            float longitude = 0.0F;
            float latitude = 0.0F;
            byte[] longitudeBytes = new byte[4];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, longitudeBytes.length, longitudeBytes, 0)[0];
            longitude = ByteUtils.byte4TofloatIs(longitudeBytes, 0);
            byte[] latitudeBytes = new byte[4];
            inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, latitudeBytes.length, latitudeBytes, 0)[0];
            latitude = ByteUtils.byte4TofloatIs(latitudeBytes, 0);
            bean.setLatitude(latitude);
            bean.setLongitude(longitude);
            if (CodecVersion.ICCID.equals(version)) {
                byte[] ICCIDBytes = new byte[20];
                inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, ICCIDBytes.length, ICCIDBytes, 0)[0];
                bean.setICCID(new String(ICCIDBytes, "UTF-8"));
            }
        } catch (Exception e) {
            logger.warn("gps定位数据上传解析出错！", e);
            return null;
        }
        return bean;
    }

    /**
     * @param cmdType  命令类型
     * @param cmdValue 命令值
     * @param mid      会话id
     * @return
     * @throws Exception
     * @Description (生成命令)
     */
    @Override
    public byte[] generateComanmd(CmdType cmdType, int cmdValue, int mid, CodecVersion codecVersion) {
        byte[] output = null;
        Date reqTime = new Date();
        try {
            if (CmdType.INTERVALTIME.equals(cmdType)) {
                output = new byte[10];
                int outputIndex = 0;
                byte[] codeBytes = new byte[1];
                ByteUtils.intTobyte1(3, codeBytes, 0);
                outputIndex = ByteUtils.copyArrays(codeBytes, 0, codeBytes.length, output, outputIndex)[1];
                byte[] midBytes = new byte[2];
                ByteUtils.intTobyte2(mid, midBytes, 0);
                outputIndex = ByteUtils.copyArrays(midBytes, 0, midBytes.length, output, ++outputIndex)[1];
                byte[] reqTimeBytes = new byte[4];
                ByteUtils.longTobyte4(reqTime.getTime() / 1000L, reqTimeBytes, 0);
                outputIndex = ByteUtils.copyArrays(reqTimeBytes, 0, reqTimeBytes.length, output, ++outputIndex)[1];
                byte[] intervalTimeBytes = new byte[3];
                ByteUtils.intTobyte3(cmdValue, intervalTimeBytes, 0);
                outputIndex = ByteUtils.copyArrays(intervalTimeBytes, 0, intervalTimeBytes.length, output, ++outputIndex)[1];
            } else if (CmdType.WORKPATTERN.equals(cmdType)) {
                output = new byte[8];
                int outputIndex = 0;

                byte[] codeBytes = new byte[1];// 请求码 [0]
                ByteUtils.intTobyte1(6, codeBytes, 0);// 00000011 --> 1
                outputIndex = ByteUtils.copyArrays(codeBytes, 0, codeBytes.length, output, outputIndex)[1];

                byte[] midBytes = new byte[2];// 会话id [1-2] --> 2
                ByteUtils.intTobyte2(mid, midBytes, 0);
                outputIndex = ByteUtils.copyArrays(midBytes, 0, midBytes.length, output, ++outputIndex)[1];

                byte[] reqTimeBytes = new byte[4];// 下发时间 [3-6] --> 4
                ByteUtils.longTobyte4(reqTime.getTime() / 1000, reqTimeBytes, 0);
                outputIndex = ByteUtils.copyArrays(reqTimeBytes, 0, reqTimeBytes.length, output, ++outputIndex)[1];

                byte[] workTypeBytes = new byte[1];// 工作模式 [7] --> 1
                ByteUtils.intTobyte1(cmdValue, workTypeBytes, 0);
                outputIndex = ByteUtils.copyArrays(workTypeBytes, 0, workTypeBytes.length, output, ++outputIndex)[1];

            } else if (CmdType.STARTING_UP.equals(cmdType)) {
                output = new byte[6];
                output[0] = 9;
                byte[] rspTimeBytes = new byte[4];
                ByteUtils.longTobyte4(reqTime.getTime() / 1000L, rspTimeBytes, 0);
                ByteUtils.copyArrays(rspTimeBytes, 0, rspTimeBytes.length, output, 1);
                output[5] = 1 == cmdValue ? (byte) 1 : (byte) 0;
            }

            switch (codecVersion) {
                case CODEC_VERSION:
                    byte[] toArr = new byte[output.length + 1];
                    ByteUtils.copyArrays(output, 0, output.length, toArr, 1);
                    ByteUtils.intTobyte1(170, toArr, 0);
                    output = toArr;
                    break;
            }

        } catch (Exception e) {
            logger.warn("命令拼装错误！");
            return null;
        }
        return output;
    }


    /**
     * @param inputBinary
     * @param inputBinaryindex
     * @param uploadBean
     * @param version
     * @return
     * @throws UnsupportedEncodingException 异常表示解析出错
     * @Description (解码请求 imei 电量 厂商编号 产品型号 数据采集时间)
     */
    private int codecBasic(byte[] inputBinary, int inputBinaryindex, UploadBean uploadBean, CodecVersion version) throws RuntimeException, UnsupportedEncodingException {
        switch (version) {
            case CODEC_VERSION:
                return codecBasic_v170(inputBinary, inputBinaryindex, uploadBean);
            case VOLTAGE:
                return codecBasic_v160(inputBinary, inputBinaryindex, uploadBean);
            case ICCID:
                return codecBasic_v150(inputBinary, inputBinaryindex, uploadBean);
            case BASIC:
                return codecBasic_v150(inputBinary, inputBinaryindex, uploadBean);
        }
        if (inputBinary == null || inputBinary.length < 21 || uploadBean == null)
            throw new RuntimeException();
        return codecBasic_v150(inputBinary, inputBinaryindex, uploadBean);
    }

    /**
     * @param inputBinary
     * @param inputBinaryindex
     * @param uploadBean
     * @return
     * @throws UnsupportedEncodingException 异常表示解析出错
     * @Description (解码请求 imei 电量 厂商编号 产品型号 数据采集时间)
     */
    private int codecBasic_v150(byte[] inputBinary, int inputBinaryindex, UploadBean uploadBean) throws RuntimeException, UnsupportedEncodingException {
        inputBinaryindex = codecBasic_prefix(inputBinary, inputBinaryindex, uploadBean);
        return codecBasic_subffix(inputBinary, inputBinaryindex, uploadBean);
    }

    /**
     * 解码basic前部分
     *
     * @param inputBinary
     * @param inputBinaryindex
     * @param uploadBean
     * @return
     * @throws RuntimeException
     * @throws UnsupportedEncodingException
     */
    private int codecBasic_prefix(byte[] inputBinary, int inputBinaryindex, UploadBean uploadBean) throws RuntimeException, UnsupportedEncodingException {
        byte[] IMEIBytes = new byte[8];
        inputBinaryindex = ByteUtils.copyArrays(inputBinary, inputBinaryindex, IMEIBytes.length, IMEIBytes, 0)[0];
        ByteUtils.chckAndFillBytes(IMEIBytes);
        String imei = ByteUtils.byte2ToIntegerStr(IMEIBytes, 0, IMEIBytes.length);
        StringBuffer sb = new StringBuffer();
        char[] imeiChar = imei.toCharArray();
        for (int i = 1; i < imeiChar.length; i++)
            sb.append(imeiChar[i]);
        imei = sb.toString();
        uploadBean.setImei(imei);
        byte[] electricQuantityBytes = new byte[1];
        inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, electricQuantityBytes.length, electricQuantityBytes, 0)[0];
        if (!ByteUtils.chckBytes(electricQuantityBytes))
            throw new RuntimeException();
        int electricQuantity = ByteUtils.byte1Toint(electricQuantityBytes, 0);
        uploadBean.setElectricQuantity(Double.valueOf(electricQuantity));
        return inputBinaryindex;
    }

    /**
     * 解码basic后部分
     *
     * @param inputBinary
     * @param inputBinaryindex
     * @param uploadBean
     * @return
     * @throws RuntimeException
     * @throws UnsupportedEncodingException
     */
    private int codecBasic_subffix(byte[] inputBinary, int inputBinaryindex, UploadBean uploadBean) throws RuntimeException, UnsupportedEncodingException {
        byte[] factoryNoBytes = new byte[4];
        inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, factoryNoBytes.length, factoryNoBytes, 0)[0];
        if (!ByteUtils.chckBytes(factoryNoBytes))
            throw new RuntimeException();
        uploadBean.setFactoryNo(new String(factoryNoBytes, "UTF-8"));
        byte[] deviceTypeBytes = new byte[4];
        inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, deviceTypeBytes.length, deviceTypeBytes, 0)[0];
        if (!ByteUtils.chckBytes(deviceTypeBytes))
            throw new RuntimeException();
        uploadBean.setDeviceType(new String(deviceTypeBytes, "UTF-8"));
        byte[] upLoadTimeBytes = new byte[4];
        inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, upLoadTimeBytes.length, upLoadTimeBytes, 0)[0];
        if (!ByteUtils.chckBytes(upLoadTimeBytes))
            throw new RuntimeException();
        long upLoadTime = ByteUtils.byte4Tolong(upLoadTimeBytes, 0);
        uploadBean.setUpLoadTime(new Date(upLoadTime * 1000L));
        return inputBinaryindex;
    }

    private int codecBasic_v160(byte[] inputBinary, int inputBinaryindex, UploadBean uploadBean) throws RuntimeException, UnsupportedEncodingException {
        if (inputBinary == null || inputBinary.length < 23 || uploadBean == null)
            throw new RuntimeException();
        inputBinaryindex = codecBasic_prefix(inputBinary, inputBinaryindex, uploadBean);
        byte[] voltageBytes = new byte[2];
        inputBinaryindex = ByteUtils.copyArrays(inputBinary, ++inputBinaryindex, voltageBytes.length, voltageBytes, 0)[0];
        if (!ByteUtils.chckBytes(voltageBytes))
            throw new RuntimeException();
        int voltage = ByteUtils.byte2Toint(voltageBytes, 0);
        uploadBean.setVoltage(Integer.valueOf(voltage));
        return codecBasic_subffix(inputBinary, inputBinaryindex, uploadBean);
    }

    private int codecBasic_v170(byte[] inputBinary, int inputBinaryindex, UploadBean uploadBean) throws RuntimeException, UnsupportedEncodingException {
        return codecBasic_v160(inputBinary, inputBinaryindex, uploadBean);
    }

}
