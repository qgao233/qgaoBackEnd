package com.qgao.springcloud.handler;

import com.alibaba.fastjson.JSON;
import com.qgao.springcloud.utils.entity.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Component
public class VisitArticleWebSocketHandler implements WebSocketHandler {

    /*根据canvas指纹保存会话，<articleId,List<canvasHash,List<WebSocketSession>>>*/
    private static final Map<String,Object> VISIT_ARTICLE_SESSIONS = new ConcurrentHashMap<>();
    private static final Map<String,AtomicInteger> VISIT_ARTICLE_RECORD = new ConcurrentHashMap<>();
    private static final String ARTICLE_PARAM = "article_id";
    private static final String FP_PARAM = "canvas_fp";



    @Override
    public void afterConnectionEstablished(WebSocketSession webSocketSession) throws Exception {
        String param = webSocketSession.getUri().toString().split("\\?")[1];
        if(StringUtils.isEmpty(param)){
            log.error("parameter is null.");
            return;
        }

        //检查参数
        Map<String,String> paramsMap = checkParam(param);
        if(paramsMap == null){
            return;
        }

        String articleId = paramsMap.get(ARTICLE_PARAM);
        Map<String, Object> canvasMap = (Map<String, Object>) VISIT_ARTICLE_SESSIONS.get(articleId);
        String canvasFP = paramsMap.get(FP_PARAM);
        if(canvasMap!=null){
            if(canvasMap.get(canvasFP) != null){
                List<WebSocketSession> list = (List<WebSocketSession>) canvasMap.get(canvasFP);
                list.add(webSocketSession);
                return;
            }
        }else{
            canvasMap = new HashMap<>();
            VISIT_ARTICLE_RECORD.put(articleId,new AtomicInteger(0));
        }
        //新上线
        List<WebSocketSession> list = new ArrayList<>();
        list.add(webSocketSession);
        canvasMap.put(canvasFP,list);
        VISIT_ARTICLE_SESSIONS.put(articleId,canvasMap);
        //增加人数
        VISIT_ARTICLE_RECORD.get(articleId).getAndIncrement();
        log.debug("connection up, {}: {}, {}: {}",ARTICLE_PARAM,articleId,FP_PARAM,canvasFP);
        //消息推送
        groupMessage(articleId,new CommonResult(200,"new visit",VISIT_ARTICLE_RECORD.get(articleId).get()));
    }

    @Override
    public void handleMessage(WebSocketSession webSocketSession, WebSocketMessage<?> webSocketMessage) throws Exception {

    }

    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {

    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public Map<String,String> checkParam(String param){
        Map<String, String> paramsMap = new HashMap<>(2);
        paramsMap.put(ARTICLE_PARAM,"");
        paramsMap.put(FP_PARAM,"");

        String[] params = param.split("&");
        int paramsLen = params.length;
        int size = paramsMap.size();
        int len = size > paramsLen ? size : paramsLen;
        for(int i = 0; i < len; i++){
            String[] array;
            try {
                array = params[i].split("=");
            }catch (IndexOutOfBoundsException e){
                log.error("parameter name is not enough.");
                return null;
            }
            if(paramsMap.get(array[0]) == null){
                continue;
            }
            paramsMap.put(array[0],array[1]);
        }
        for(Map.Entry<String,String> map : paramsMap.entrySet()){
            if(StringUtils.isEmpty(map.getValue())){
                log.error("parameter name is missing: {}",map.getKey());
                return null;
            }
        }
        return paramsMap;
    }

    /*
    群发消息：对所有在线的用户发送消息
     */
    public void groupMessage(String articleId, CommonResult commonResult) {
        List<String> expiredCanvasFPs = new ArrayList<>();
        Set<String> canvasFPs = ((Map<String,Object>)VISIT_ARTICLE_SESSIONS.get(articleId)).keySet();
        for(String canvasFP : canvasFPs){
            if(!sendMessage(articleId, canvasFP, commonResult)){
                expiredCanvasFPs.add(canvasFP);
                //人数-1
                VISIT_ARTICLE_RECORD.get(articleId).getAndDecrement();
                commonResult = new CommonResult(200,"visit down",VISIT_ARTICLE_RECORD.get(articleId).get());
            }
        }
        for(String canvasFP : expiredCanvasFPs){
            ((Map<String,Object>)VISIT_ARTICLE_SESSIONS.get(articleId)).remove(canvasFP);
        }
    }

    public boolean sendMessage(String articleId, String canvasFP, CommonResult commonResult) {

        if(((Map<String,Object>)VISIT_ARTICLE_SESSIONS.get(articleId)).get(canvasFP) == null) return false;
        List<WebSocketSession> list = (List<WebSocketSession>) ((Map<String,Object>)VISIT_ARTICLE_SESSIONS.get(articleId)).get(canvasFP);
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
