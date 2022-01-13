package com.qgao.springcloud.handler;

import com.alibaba.fastjson.JSON;
import com.qgao.springcloud.utils.entity.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class VisitWebSocketHandler implements WebSocketHandler {

    /*根据canvas指纹保存会话，<canvasHash,List<WebSocketSession>>*/
    private static final Map<String,Object> VISIT_SESSIONS = new ConcurrentHashMap<>();
    private static final AtomicInteger VISIT_RECORD = new AtomicInteger(0);
    private static final String PARAM = "canvas_fp";

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

        String canvasFP = paramsArray[1];

        if(VISIT_SESSIONS.get(canvasFP) != null){
            List<WebSocketSession> list = (List<WebSocketSession>) VISIT_SESSIONS.get(canvasFP);
            list.add(webSocketSession);
        }else{
            //新上线
            List<WebSocketSession> list = new ArrayList<>();
            list.add(webSocketSession);
            VISIT_SESSIONS.put(canvasFP,list);
            //增加人数
            VISIT_RECORD.getAndIncrement();
            log.debug("connection up, {}: {}",PARAM,canvasFP);
            //消息推送
            groupMessage(new CommonResult(200,"new visit",VISIT_RECORD.get()));
        }
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {
        log.debug("handle message.");
    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        log.error("connection error.");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.debug("connection down.");
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /*
    群发消息：对所有在线的用户发送消息
     */
    public void groupMessage(CommonResult commonResult) {
        List<String> expiredCanvasFPs = new ArrayList<>();
        Set<String> canvasFPs = VISIT_SESSIONS.keySet();
        for(String canvasFP : canvasFPs){
            if(!sendMessage(canvasFP, commonResult)){
                expiredCanvasFPs.add(canvasFP);
                //人数-1
                VISIT_RECORD.getAndDecrement();
                commonResult = new CommonResult(200,"visit down",VISIT_RECORD.get());
            }
        }
        for(String canvasFP : expiredCanvasFPs){
            VISIT_SESSIONS.remove(canvasFP);
        }
    }

    public boolean sendMessage(String canvasFP, CommonResult commonResult) {

        if(VISIT_SESSIONS.get(canvasFP) == null) return false;
        List<WebSocketSession> list = (List<WebSocketSession>) VISIT_SESSIONS.get(canvasFP);
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
            return false;
        } else{
            //删除部分过期的session
            list.removeAll(expiredList);
        }

        return true;
    }
}
