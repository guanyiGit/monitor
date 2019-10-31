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
public class TDeviceRecord extends Model<TDeviceRecord> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ID_WORKER)
    private String id;

    private Double lng;

    private Double lat;

    private Double quantity;

    private String deviceNo;

    private LocalDateTime createDate;

    private LocalDateTime dataTime;

    private LocalDateTime uploadTime;

    /**
     * 0 gps 1wifi 2 simple 3warn 4 start
     */
    private Integer dataType;

    private String traceId;

    private String iccid;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
