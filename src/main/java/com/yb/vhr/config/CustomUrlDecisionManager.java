package com.yb.vhr.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Collection;

/**
 * 判断当前用户是否具备请求对应的 role
 */
@Component
public class CustomUrlDecisionManager implements AccessDecisionManager {
    /**
     *
     * @param authentication        保存登录用户信息
     * @param o
     * @param configAttributes        MyFilter 的 getAttributes 方法的返回值
     * @throws AccessDeniedException
     * @throws InsufficientAuthenticationException
     */
    @Override
    public void decide(Authentication authentication, Object o, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        for(ConfigAttribute configAttribute : configAttributes){      //遍历 roles
            String needRole = configAttribute.getAttribute();       //访问资源需要的角色
            if("ROLE_LOGIN".equals(needRole)){      //登录后访问
                //当前用户是匿名用户示例   ====》未登录
                if(authentication instanceof AnonymousAuthenticationToken){
                    throw new AccessDeniedException("尚未登录，请登录！");
                } else{
                    return;     //登录过，直接返回
                }
            }
            //  获取当前登录用户的角色
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for(GrantedAuthority authority : authorities){
                if(authority.getAuthority().equals(needRole)){       //相等    ===》 具备需要的角色
                    return;
                }
            }
        }
        throw new AccessDeniedException("不具备权限，请联系管理员获取权限！");
    }

    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }
}
