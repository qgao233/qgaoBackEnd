package com.qgao.springcloud;

import com.qgao.springcloud.utils.util.EncryptUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ArticleMain8001.class})
public class IdTest {

    @Test
    public void getMd5Pwd(){
        System.out.println(EncryptUtil.calcMD5("123"));
    }
}
