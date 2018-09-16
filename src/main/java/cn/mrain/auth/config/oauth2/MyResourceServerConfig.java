package cn.mrain.auth.config.oauth2;

import cn.mrain.auth.config.authentication.MyAuthenticationFailureHandle;
import cn.mrain.auth.config.authentication.MyAuthenticationSuccessHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * @author 22
 * Security OAuth2 资源服务器 配置
 */
@Configuration
@EnableResourceServer
public class MyResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private MyAuthenticationSuccessHandle myAuthenticationSuccessHandle;
    @Autowired
    private MyAuthenticationFailureHandle myAuthenticationFailureHandle;

    @Override
    public void configure(HttpSecurity http) throws Exception {

        //表单登录
        http.formLogin()
                .loginProcessingUrl("/login")
                .loginPage("/")
                .successHandler(myAuthenticationSuccessHandle)
                .failureHandler(myAuthenticationFailureHandle);
        // 路径认证相关
        http.authorizeRequests()
                .antMatchers("/",
                        "/webjars/**").permitAll()
                //其他地址的访问均需验证权限
                .anyRequest().authenticated();
//        禁用session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        // 关闭csrf
//        http.csrf().disable();
    }


}
