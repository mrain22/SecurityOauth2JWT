package cn.mrain.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author 22
 */
@Configuration
public class SecurityConfig {

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        //表单登录
//        http.formLogin();
//        // 路径认证相关
//        http.authorizeRequests()
//                .antMatchers("/",
//                        "/webjars/**").permitAll()
//                //其他地址的访问均需验证权限
//                .anyRequest().authenticated();
//        // 关闭csrf
//        http.csrf().disable();
//    }

    /*   密码加盐加密*/
    @Bean
    public PasswordEncoder passwordEncoder (){
        //Spring自带的每次会随机生成盐值，即使密码相同，加密后也不同
        return  new BCryptPasswordEncoder();
    }
}
