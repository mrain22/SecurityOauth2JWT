package cn.mrain.auth.config.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author 22
 * 授权成功处理器
 */
@Component("myAuthenticationSuccessHandle")
public class MyAuthenticationSuccessHandle implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * spring管理的
     */
    @Autowired
    private ClientDetailsService clientDetailsService;
    /**
     *
     */
    @Autowired
    private AuthorizationServerTokenServices authorizationServerTokenServices;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        logger.info("登录成功！");

        String header = request.getHeader("Authorization");
        if (header == null || !header.toLowerCase().startsWith("basic ")) {
            throw new UnapprovedClientAuthenticationException("请求头中无client信息");
        }

        String[] tokens = extractAndDecodeHeader(header, request);
        assert tokens.length == 2;

        String clientId = tokens[0];
        String clientSecret = tokens[1];
        /**
         * 此处通过前台传来的clientid查询服务器中配置的client信息。
         * 1、拿到clientDetails
         */
        ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new UnapprovedClientAuthenticationException("clientId对应的信息为空！" + clientId);
        } else if (!(StringUtils.equals(clientDetails.getClientSecret(),clientSecret))){
            /*比较前台传来的clientSecret是否和服务端配置的clientSecret匹配*/
            throw new UnapprovedClientAuthenticationException("clientSecret不匹配");
        }
        /**
         * 2、new TokenRequest
         */
        TokenRequest tokenRequest = new TokenRequest(new HashMap<>(),clientId,clientDetails.getScope(),"custom");

        /**
         * 3、构建OAuth2Request
         */
        OAuth2Request oAuth2Request = tokenRequest.createOAuth2Request(clientDetails);

        /**
         * 4、拼接OAuth2Authentication
         */
        OAuth2Authentication oAuth2Authentication = new OAuth2Authentication(oAuth2Request,authentication);
        /**
         * 5、利用AuthorizationServerTokenServices生成OAuth2AccessToken
         */
        OAuth2AccessToken accessToken = authorizationServerTokenServices.createAccessToken(oAuth2Authentication);

        /**
         * 6、通过json形式写回
         */
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(accessToken));


    }

    /**
     * 用于解码头部信息  从BasicAuthenticationFilter复制过来的
     *
     * @param header
     * @param request
     * @return
     * @throws IOException
     */

    private String[] extractAndDecodeHeader(String header, HttpServletRequest request)
            throws IOException {

        byte[] base64Token = header.substring(6).getBytes("UTF-8");
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(
                    "Failed to decode basic authentication token");
        }

        String token = new String(decoded, "UTF-8");

        int delim = token.indexOf(":");

        if (delim == -1) {
            throw new BadCredentialsException("Invalid basic authentication token");
        }
        return new String[]{token.substring(0, delim), token.substring(delim + 1)};
    }
}