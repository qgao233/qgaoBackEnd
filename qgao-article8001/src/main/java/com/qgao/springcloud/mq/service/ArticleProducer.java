package com.qgao.springcloud.mq.service;

import com.qgao.springcloud.utils.enums.UserNotifyTypeEnum;
import com.qgao.springcloud.utils.entity.UserNotify;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
@Slf4j
public class ArticleProducer {

    @Value("${qgao.rocketmq.topic.notify}")
    private String notifyTopic;
    @Value("${qgao.rocketmq.tag.notify.article}")
    private String articleTag;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public void sendMessageWithNewArticle(Long userId, Long articleId){
        log.debug("produce message");


        UserNotify userNotify = new UserNotify();
        userNotify.setNotifyType(UserNotifyTypeEnum.ARTICLE.getCode());
        userNotify.setNotifyFromUserId(userId);
        userNotify.setNotifyContent(String.valueOf(articleId));
        userNotify.setNotifyDate(new Date());

        rocketMQTemplate.asyncSend(notifyTopic + ":" + articleTag, userNotify, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                log.debug("result: {}",sendResult);
            }

            @Override
            public void onException(Throwable throwable) {
                log.error("send async message failure,"+throwable.getMessage());
            }
        });
    }
}
