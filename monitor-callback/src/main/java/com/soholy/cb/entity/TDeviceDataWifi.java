package com.soholy.cb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class TDeviceDataWifi extends Model<TDeviceDataWifi> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "device_data_wifi_id", type = IdType.ID_WORKER)
    private String deviceDataWifiId;

    private String bssid;

    private Float rssi;

    private String deviceDataId;

    private Long deviceNo;

    private LocalDateTime creationTime;

    /**
     * 0未转换，1已成功转换，2转换失败
     */
    private Integer mark;


    @Override
    protected Serializable pkVal() {
        return this.deviceDataWifiId;
    }

}
