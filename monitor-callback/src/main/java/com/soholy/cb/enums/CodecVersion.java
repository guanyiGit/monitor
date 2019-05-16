package com.soholy.cb.enums;

/**
 * 解码版本号
 */
public enum CodecVersion {
    /**
     * 基础版本
     */
    BASIC(1.4f),
    /**
     * 1.5
     */
    ICCID(1.5f),

    /**
     * 1.6
     */
    VOLTAGE(1.6f),

    /**
     * 1.7
     */
    CODEC_VERSION(1.7f);


    private float version;

    private CodecVersion(float version) {
        this.version = version;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }
}
