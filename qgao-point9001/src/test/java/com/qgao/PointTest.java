package com.qgao;

import com.qgao.springcloud.PointMain9001;
import com.qgao.springcloud.dto.LevelPointDto;
import com.qgao.springcloud.service.PointService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.Serializable;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PointMain9001.class})
public class PointTest {

    @Resource
    private PointService pointService;

    @Test
    public void getUserLevelAndPoint() throws Exception{
        pointService.increaseUserPoint(233l,2);
    }

    @Test
    public void test() throws Exception{
        pointService.test();
    }
}
