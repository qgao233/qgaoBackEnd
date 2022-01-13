package com.qgao.springcloud;

import com.qgao.springcloud.mq.service.ArticleProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ArticleMain8001.class})
public class MQTest {

    @Resource
    private ArticleProducer articleProducer;

    @Test
    public void sendMessage(){
        articleProducer.sendMessageWithNewArticle(0l,333l);
    }
}
