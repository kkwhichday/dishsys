package com.macro.mall.cluster.config;


import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.UmsAdminService;
import com.macro.mall.util.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 自定义的握手拦截
 *  在握手前判断，判断当前用户是否已经登录。如果未登录，则不允许登录websocket
 * Created by huangrongyou@yixin.im on 2018/7/10.
 */
@Component
public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthHandshakeInterceptor.class);

    @Autowired
    UmsAdminService umsAdminService;
    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        HttpSession httpSession = getSession(request);
        String token = getToken(request.getURI().toString());
        token =URLDecoder.decode(token);
        check(token);



        UmsAdmin umsAdmin = umsAdminService.getCurrentUmsAdmin();
        if(umsAdmin==null){
            log.error("未登录系统，禁止登录websocket!");
            return false;
        }else{
            String user = umsAdmin.getUsername();
            if(StringUtils.isEmpty(user)){
                log.error("未登录系统，禁止登录websocket!");
                return false;
            }
            log.info("login = " + user);
        }
        return true;
    }
     private String getToken(String uri) {
         int index = uri.indexOf("accessToken=");

         return uri.substring(index + 12, uri.length());
     }


     private Boolean check(String authHeader){
         if (authHeader != null && authHeader.startsWith(this.tokenHead)) {
             String authToken = authHeader.substring(this.tokenHead.length());// The part after "Bearer "
             String username = jwtTokenUtil.getUserNameFromToken(authToken);
//            LOGGER.info("checking username:{}", username);
             if (username != null &&
                     (SecurityContextHolder.getContext().getAuthentication() == null
                             ||SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal().equals("anonymousUser")
             )) {
                 UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                 if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                     UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                    LOGGER.info("authenticated user:{}", username);
                     SecurityContextHolder.getContext().setAuthentication(authentication);
                     return true;
                 }
             }
         }
         return false;
     }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }

    // 参考 HttpSessionHandshakeInterceptor
    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
