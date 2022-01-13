package com.qgao.springcloud;

import com.qgao.springcloud.helper.MailHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {UserMain8004.class})
public class MailSendTest {

    @Resource
    private MailHelper mailHelper;

    @Test
    public void testSendMail() throws Exception {
        String mail = "527629463@qq.com";
        String subject = "注册qgao";
        String frontPageHost = "127.0.0.1";


        mailHelper.sendMailWithBanner(mail,subject,frontPageHost);
    }
}
