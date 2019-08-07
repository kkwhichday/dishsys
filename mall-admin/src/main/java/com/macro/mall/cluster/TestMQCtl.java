package com.macro.mall.cluster;


import com.macro.mall.cluster.model.RequestMessage;
import com.macro.mall.cluster.redis.IRedisSessionService;
import com.macro.mall.dto.CommonResult;
import com.macro.mall.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by huangrongyou@yixin.im on 2018/7/9.
 */
@Controller
@RequestMapping(value = "/ws")
public class TestMQCtl {
    private  static final Logger log = LoggerFactory.getLogger(TestMQCtl.class);
//    @Autowired
//    private SimpMessagingTemplate template;
    // 实现spring websocket需要引入AmqpTemplate类
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private IRedisSessionService redisSessionService;



    /**
     * 检查这个用户是否在线
     * @param username
     * @return true 在线  false 离线
     */
    @GetMapping("/check")
    @ResponseBody
    public Object checkOnline(String username){
        // 根据用户名称获取用户对应的session id值
        String wsSessionId = redisSessionService.get(username);
        return new CommonResult().success(Optional.ofNullable(wsSessionId).isPresent());
    }


    @GetMapping("/getUserList")
    @ResponseBody
    public Object getUserList(){
        // 根据用户名称获取用户对应的session id值
        Set<String> set = redisSessionService.getKeys("chat:*");
        List<String> list= set.stream().map(o-> o.substring(5)).collect(Collectors.toList());

        return new CommonResult().success(list);
    }


    /**
     * 向执行用户发送请求
     * @param requestMessage
     * @return
     */
    @PostMapping(value = "send2user")
    @ResponseBody
    public Object sendMq2User(@RequestBody  RequestMessage requestMessage){
        String name =requestMessage.getToUserName();
        // 根据用户名称获取用户对应的session id值
        String wsSessionId = redisSessionService.get(name);

        if(wsSessionId==null){
            log.info("用户[{}]不在线"+name);
            return new CommonResult().failed();
        }

        // 生成路由键值，生成规则如下: websocket订阅的目的地 + "-user" + websocket的sessionId值。生成值类似:
        String routingKey = getTopicRoutingKey("demo", wsSessionId);
        // 向amq.topi交换机发送消息，路由键为routingKey
        log.info("向用户[{}]sessionId=[{}]，发送消息[{}]，路由键[{}]", name, wsSessionId, requestMessage, routingKey);
        amqpTemplate.convertAndSend("amq.topic", routingKey, JsonUtil.objectToJson(requestMessage));
        return new CommonResult().success("发送成功");
    }



    /**
     * 获取Topic的生成的路由键
     *
     * @param actualDestination
     * @param sessionId
     * @return
     */
    private String getTopicRoutingKey(String actualDestination, String sessionId){
        return actualDestination + "-user" + sessionId;
    }

}
