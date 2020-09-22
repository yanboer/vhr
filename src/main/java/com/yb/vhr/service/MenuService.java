package com.yb.vhr.service;

import com.yb.vhr.mapper.MenuMapper;
import com.yb.vhr.model.Hr;
import com.yb.vhr.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuService {
    @Autowired
    MenuMapper menuMapper;

    public List<Menu> getMenusByHrId(){
        return menuMapper.getMenusByHrId(((Hr)SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getId());
    }

    //  ====> 查询结果加入缓存中，由于 menus 数据不经常变化，每次都从数据库中查询会对数据库造成过大的压力，因此放入缓存中
//    @Cacheable
    public List<Menu> getAllMenusWithRole(){
        return menuMapper.getAllMenusWithRole();
    }
}
