package com.yb.vhr.service;

import com.yb.vhr.mapper.MenuMapper;
import com.yb.vhr.model.Hr;
import com.yb.vhr.model.Menu;
import org.springframework.beans.factory.annotation.Autowired;
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
}
