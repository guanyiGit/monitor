package com.soholy.cb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author GuanY
 * @since 2019-05-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class TDevice extends Model<TDevice> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 站点编号
     */
    private String site;

    /**
     * 设备编号
     */
    private String deviceNumber;

    /**
     * 通信方式1短信方式2PS域方式3CS域方式
     */
    private Integer communicateMode;

    /**
     * 设备厂商代码
     */
    private String deviceCode;

    /**
     * 设备种类
     */
    private Integer deviceCalss;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 设备状态0删除1正常2异常3离线
     */
    private Integer state;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 设备生产序列号
     */
    private String deviceProductionNumber;

    /**
     * 安装单位
     */
    private String installUnit;

    /**
     * 安装人
     */
    private String installer;

    /**
     * 创建人
     */
    private Integer userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 设备名称
     */
    private String deviceName;

    private String iccid;

    /**
     * 平台id
     */
    @TableField("device_Id_Iot")
    private String deviceIdIot;

    /**
     * 设备状态 0未激活 1已激活
     */
    private Integer status;

    /**
     * 溯源id
     */
    @TableField("TraceId")
    private String TraceId;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
