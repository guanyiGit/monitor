package com.soholy.cb.dao.otherDb;

import com.soholy.cb.entity.otherDb.po.Wifi;
import com.soholy.cb.entity.otherDb.po.WifiExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WifiMapper {
    int countByExample(WifiExample example);

    int deleteByExample(WifiExample example);

    int deleteByPrimaryKey(String priId);

    int insert(Wifi record);

    int insertSelective(Wifi record);

    List<Wifi> selectByExample(WifiExample example);

    Wifi selectByPrimaryKey(String priId);

    int updateByExampleSelective(@Param("record") Wifi record, @Param("example") WifiExample example);

    int updateByExample(@Param("record") Wifi record, @Param("example") WifiExample example);

    int updateByPrimaryKeySelective(Wifi record);

    int updateByPrimaryKey(Wifi record);
}