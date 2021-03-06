package com.soholy.cb.entity.app.triggerSource;

public class Device_TriggerSource implements TriggerSource {

    // 必选 Enum 设备触发源类型 DEVICE
    private TriggerSourceType type;
    // 必选 String 设备Id(1-36)
    private String deviceId;
    // 必选 String 服务id(1-128)
    private String serviceId;

    public TriggerSourceType getType() {
        return type;
    }

    public void setType(TriggerSourceType type) {
        this.type = type;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

}
