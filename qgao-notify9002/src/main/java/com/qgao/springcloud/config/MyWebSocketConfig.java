package com.qgao.springcloud.config;

import com.qgao.springcloud.handler.OnlineWebSocketHandler;
import com.qgao.springcloud.handler.VisitArticleWebSocketHandler;
import com.qgao.springcloud.handler.VisitWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

@Configuration
public class MyWebSocketConfig implements WebSocketConfigurer{

    @Resource
    private OnlineWebSocketHandler onlineWebSocketHandler;
    @Resource
    private VisitWebSocketHandler visitWebSocketHandler;
    @Resource
    private VisitArticleWebSocketHandler visitArticleWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(onlineWebSocketHandler, "/online")//设置连接路径和处理类
                .addHandler(visitWebSocketHandler,"/visit")
                .addHandler(visitArticleWebSocketHandler,"/visit/article")
                .setAllowedOrigins("*");
    }
}
