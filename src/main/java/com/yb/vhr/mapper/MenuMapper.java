package com.yb.vhr.mapper;

import com.yb.vhr.model.Hr;
import com.yb.vhr.model.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Menu record);

    int updateByPrimaryKey(Menu record);


    List<Menu> getMenusByHrId(Integer hrid);
}