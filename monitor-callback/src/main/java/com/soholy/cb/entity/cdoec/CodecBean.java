package com.soholy.cb.entity.cdoec;

public class CodecBean extends CodecBeanV1_7{

    private UploadBean uploadBean;
    private DecodeRsp decodeRsp;

    public UploadBean getUploadBean() {
        return uploadBean;
    }

    public void setUploadBean(UploadBean uploadBean) {
        this.uploadBean = uploadBean;
    }

    public DecodeRsp getDecodeRsp() {
        return decodeRsp;
    }

    public void setDecodeRsp(DecodeRsp decodeRsp) {
        this.decodeRsp = decodeRsp;
    }

}
