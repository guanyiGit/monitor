package com.soholy.cb.service.codec;


import com.soholy.cb.entity.cdoec.CodecBean;
import com.soholy.cb.enums.CmdType;
import com.soholy.cb.enums.CodecVersion;

public interface CodecService {

    /**
     * @param inputBinary
     * @return
     * @throws Exception
     * @Description (解析数据)
     */
    CodecBean decodec(byte[] inputBinary);



    /**
     * @param cmdType  命令类型
     * @param cmdValue 命令值
     * @param mid      命令id
     * @return
     * @Description (生产响应命令)
     */
    byte[] generateComanmd(CmdType cmdType, int cmdValue, int mid);

}
