package com.macro.mall.cluster.config;


import com.macro.mall.cluster.MyPrincipal;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.UmsAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

/**
 *  处理WebSocket的握手请求
 *      定义自己的Principal
 * Created by huangrongyou@yixin.im on 2018/7/10.
 */
@Component
public class MyPrincipalHandshakeHandler extends DefaultHandshakeHandler {
    private static final Logger log = LoggerFactory.getLogger(MyPrincipalHandshakeHandler.class);

    @Autowired
    UmsAdminService umsAdminService;
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {

//        HttpSession httpSession = getSession(request);
        UmsAdmin umsAdmin = umsAdminService.getCurrentUmsAdmin();
        if(umsAdmin==null){
            throw new RuntimeException("未登录系统，禁止登录websocket!");
        }
        String user = umsAdmin.getUsername();

        log.info(" MyDefaultHandshakeHandler login = " + user);
        return new MyPrincipal(user);
    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }
}
