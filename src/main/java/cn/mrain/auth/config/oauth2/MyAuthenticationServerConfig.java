package cn.mrain.auth.config.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 22
 * Security OAuth2 认证服务器 配置
 */
@Configuration
@EnableAuthorizationServer
public class MyAuthenticationServerConfig extends AuthorizationServerConfigurerAdapter {


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Jwt
     */
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired(required = false)
    private TokenEnhancer jwtTokenEnhancer;

    /**
     * 用来配置端点，入口点
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        /**
         * 当不继承AuthorizationServerConfigurerAdapter，认证服务器会自己在系统中找，继承后需要我们自己配置
         * tokenStore配置令牌存储的方式，这里配置了redisTokenStore，将令牌放到redis中
         */
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
//                .tokenStore(redisTokenStore());
                .tokenStore(tokenStore);
//                .tokenEnhancer(jwtTokenEnhancer)
//                .accessTokenConverter(jwtAccessTokenConverter);
        if (jwtTokenEnhancer != null) {
            //增强链
            TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
            //将增强的信息放到列表
            List<TokenEnhancer> enhancers = new ArrayList<TokenEnhancer>();
            enhancers.add(jwtTokenEnhancer);
            enhancers.add(jwtAccessTokenConverter);
            //放入到增强链
            enhancerChain.setTokenEnhancers(enhancers);

            endpoints.tokenEnhancer(enhancerChain)
                    .accessTokenConverter(jwtAccessTokenConverter);
        }
    }

    /**
     * 客户端相关的配置
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        /**
         * inMemory client信息存储的类型，inMemory存储在内存，jdbc需要指定数据源。
         * withClient和secret 相当于application.properties中的security.oauth2.client.client-id和security.oauth2.client.client-secret
         * accessTokenValiditySeconds过期时间s
         * authorizedGrantTypes 客户端支持的模式
         * scopes 权限
         * 可以配置多个Client
         */
        clients.inMemory()
                .withClient("mrain")
                .secret("123456")
                .accessTokenValiditySeconds(120)
                .authorizedGrantTypes("refresh_token", "password")
                .refreshTokenValiditySeconds(2592000)
                .scopes("all");
    }

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    /**
     * 配置RedisTokenStore
     * Security OAuth2 推荐使用RedisTokenStore
     * @return
     */
//    @Bean
//    public TokenStore redisTokenStore(){
//        return new  RedisTokenStore(redisConnectionFactory);
//    }


}
