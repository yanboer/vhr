package com.yb.vhr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yb.vhr.model.Hr;
import com.yb.vhr.model.RespBean;
import com.yb.vhr.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    HrService hrService;

    @Autowired
    CustomFilterInvocationSecurityMetadataSource customFilterInvocationSecurityMetadataSource;

    @Autowired
    CustomUrlDecisionManager customUrlDecisionManager;

    @Bean      //进行密码加密
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(hrService);
    }

    /**
     *       访问 /login 页面，不用经过 Spring Security 拦截，直接进入登录页
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/login");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
//                .anyRequest().authenticated()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(customUrlDecisionManager);
                        object.setSecurityMetadataSource(customFilterInvocationSecurityMetadataSource);
                        return object;
                    }
                })
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")
                .loginPage("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp,
                                                        Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        Hr hr = (Hr) authentication.getPrincipal();
                        hr.setPassword(null);                                  //密码不能传回前端
                        RespBean ok = RespBean.ok("登录成功", hr);      //返回用户对象
                        String s = new ObjectMapper().writeValueAsString(ok);
                        out.write(s);
                        out.flush();
                        out.close();
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp,
                                                        AuthenticationException exception) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        RespBean respBean = RespBean.error("登录失败！");
                        if(exception instanceof LockedException){
                            respBean.setMsg("账户被锁定，请联系管理员！");
                        }else if(exception instanceof CredentialsExpiredException){
                            respBean.setMsg("密码过期，请联系管理员");
                        }else if(exception instanceof AccountExpiredException){
                            respBean.setMsg("账户过期，请联系管理员！");
                        }else if(exception instanceof DisabledException){
                            respBean.setMsg("账户被禁用，请联系管理员！");
                        }else if(exception instanceof BadCredentialsException){
                            respBean.setMsg("用户名或密码输入错误，请重新输入！");
                        }
                        out.write(new ObjectMapper().writeValueAsString(respBean));
                        out.flush();
                        out.close();
                    }
                })
                .permitAll()
                .and()
                .logout()               //注销接口
                .logoutSuccessHandler(new LogoutSuccessHandler() {
                    @Override
                    public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp,
                                                Authentication authentication) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        out.write(new ObjectMapper().writeValueAsString(RespBean.ok("注销成功!")));
                        out.flush();
                        out.close();
                }
                })
                .permitAll()
                .and()
                .csrf().disable().exceptionHandling()
                //没有认证时，返回错误信息，而不是重定向到 /login 页面    ====》重定向可能产生跨域问题
                .authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException {
                        resp.setContentType("application/json;charset=utf-8");
                        PrintWriter out = resp.getWriter();
                        RespBean respBean = RespBean.error("访问失败！");
                        if(exception instanceof InsufficientAuthenticationException){
                            respBean.setMsg("权限不足，请联系管理员！");
                        }
                        out.write(new ObjectMapper().writeValueAsString(respBean));
                        out.flush();
                        out.close();
                    }
                });
    }
}
