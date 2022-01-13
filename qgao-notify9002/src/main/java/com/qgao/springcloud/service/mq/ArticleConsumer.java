package com.qgao.springcloud.service.mq;

import com.qgao.springcloud.utils.entity.UserNotify;
import com.qgao.springcloud.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
@Slf4j
@RocketMQMessageListener(consumerGroup = "${qgao.rocketmq.group.consumer.notify}",topic = "${qgao.rocketmq.topic.notify}",selectorExpression = "${qgao.rocketmq.tag.notify.article}")
public class ArticleConsumer implements RocketMQListener<UserNotify>{

    @Resource
    private NotifyService notifyService;

    @Override
    public void onMessage(UserNotify userNotify) {
        log.debug("consume message");
        notifyService.getFanAndSendNotify(userNotify);
    }
}
