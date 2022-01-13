package com.qgao.springcloud.handler;

import com.alibaba.fastjson.JSON;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class OnlineWebSocketHandler implements WebSocketHandler {

    /*保存会话，<userId,List<WebSocketSession>>*/
    private static final Map<Long, Object> SESSIONS = new ConcurrentHashMap<>();
    private static final String PARAM = "user_id";

    @Resource
    private NotifyService notifyService;
    /*
    连接建立之后
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String param = webSocketSession.getUri().toString().split("\\?")[1];
        if(StringUtils.isEmpty(param)){
            log.error("parameter is null.");
            return;
        }
        String[] paramsArray = param.split("&")[0].split("=");
        if(!paramsArray[0].equals(PARAM)){
            log.error("parameter name is not '"+PARAM+"'.");
            return;
        }
        Long userId = Long.parseLong(paramsArray[1]);

        if(SESSIONS.get(userId) != null){
            List<WebSocketSession> list = (List<WebSocketSession>) SESSIONS.get(userId);
            list.add(webSocketSession);
        }else{
            List<WebSocketSession> list = new ArrayList<>();
            list.add(webSocketSession);
            SESSIONS.put(userId,list);
            log.debug("connection up, {}: {}",PARAM,userId);
        }

        notifyService.getAndSendNotify(userId);
    }

    /*
    前台发了消息过来，处理消息
     */
    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        log.debug("handle message.");
    }

    /*
    连接出错
     */
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.error("connection error.");
    }

    /*
    前台关闭了浏览器，连接断开
     */
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
//        SESSIONS.values().remove(webSocketSession);
        log.debug("connection down.");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }



    public boolean sendMessage(Long userId, CommonResult commonResult) {

        if(SESSIONS.get(userId) == null) return false;
        List<WebSocketSession> list = (List<WebSocketSession>) SESSIONS.get(userId);
        List<WebSocketSession> expiredList = new ArrayList<>();//保存已经过期的session，之后删除
        int i = 0;
        boolean flag = false;

        try {
            for(; i < list.size();i++){
                WebSocketSession webSocketSession = list.get(i);
                if (webSocketSession == null || !webSocketSession.isOpen()){
                    //处理已经过期的session
                    expiredList.add(webSocketSession);
                    continue;
                }
                flag = true;
                webSocketSession.sendMessage(new TextMessage(JSON.toJSONString(commonResult)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(!flag){
            //全部过期
            SESSIONS.remove(userId);
            return false;
        } else{
            //删除部分过期的session
            list.removeAll(expiredList);
        }

        return true;
    }

    /*
    所有在线用户中：给某用户的粉丝发送消息
    返回不在线的用户
     */
    public List<Long> fanMessage(List<Long> userIdList, CommonResult commonResult){
        List<Long> offlineUserIds = new ArrayList<>();
        for (Long userId : userIdList){
            if(!sendMessage(userId,commonResult)){
                offlineUserIds.add(userId);
            }
        }
        return offlineUserIds;
    }

    /*
    群发消息：对所有在线的用户发送消息
     */
    public void groupMessage(CommonResult commonResult) {
        SESSIONS.keySet().forEach(us -> sendMessage(us, commonResult));
    }
}
