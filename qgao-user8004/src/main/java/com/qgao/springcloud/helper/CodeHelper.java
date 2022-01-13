package com.qgao.springcloud.helper;

import com.qgao.springcloud.dto.SwipeCodeDto;
import com.qgao.springcloud.util.RandomVerifyImgCodeUtil;
import com.qgao.springcloud.utils.util.SnowFlakeIdUtil;
import com.qgao.springcloud.util.SwipeImgCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CodeHelper {

    @Resource
    private RedisTemplate redisTemplate;
    @Value("${qgao.verification.code.expire:#{60}}")
    private int codeExpire;
    @Value("${qgao.verification.code.swipe.path:#{'static/swipecode.jpg'}")
    private String swipeCodePath;
    /*
    返回一个，redis的key,和验证码图片的base64编码
     */
    public Map<String,Object> getRandomCodeMap(){
        int charSize = 4;

        String verifyCode = RandomVerifyImgCodeUtil.generateVerifyCode(charSize);
        String tempId = String.valueOf(SnowFlakeIdUtil.generateID());


        int w = 120, h = 40;
        int type = new Random().nextInt(7);

        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("sessionId",tempId);

        ByteArrayOutputStream os;
        try {
            os = new ByteArrayOutputStream();
            switch (type){
                case 0:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "login");
                    break;
                case 1:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "GIF");
                    break;
                case 2:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "3D");
                    break;
                case 3:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "GIF3D");
                    break;
                case 4:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "mix2");
                    break;
                case 5:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "mixGIF");
                    break;
                case 6:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "coupons");
                    break;
                default:
                    RandomVerifyImgCodeUtil.outputImage(w, h, os, verifyCode, "mixGIF");
            }
            String img = Base64.getEncoder().encodeToString(os.toByteArray());
            paramsMap.put("img", img);
        }catch (Exception e){
            throw new RuntimeException("make img base64 failure",e);
        }
        //放入缓存60s
        redisTemplate.opsForValue().set(tempId,verifyCode,codeExpire, TimeUnit.SECONDS);
        return paramsMap;
    }

    public boolean checkRandomCode(String code,String sessionId){
        String cacheCode = (String) redisTemplate.opsForValue().get(sessionId);
        if(cacheCode!= null && code.equals(cacheCode)){
            return true;
        }
        return false;
    }

    public Map<String,Object> getSwipeCode() throws Exception{
        BufferedImage[] bufferedImages = null;
        int otlX = 0;
        int otlY = 0;
        int circleRadius = 0;
        synchronized (this){
            bufferedImages = SwipeImgCodeUtil.cutImage(new ClassPathResource(swipeCodePath));
            otlX = SwipeImgCodeUtil.otlX;
            otlY = SwipeImgCodeUtil.otlY;
            circleRadius = SwipeImgCodeUtil.circleRadius;
        }

        SwipeCodeDto val = new SwipeCodeDto();
        SwipeCodeDto.ImageDetail[] imgs = new SwipeCodeDto.ImageDetail[2];

        //抠出来的拼图
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImages[0],"png",os);
        byte[] maskImg = os.toByteArray();
        imgs[0] = val.new ImageDetail("mask", Base64.getEncoder().encodeToString(maskImg));
        os.flush();
        os.close();
        //背景图
        os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImages[1],"png",os);
        byte[] bgImg = os.toByteArray();
        imgs[1] = val.new ImageDetail("bg", Base64.getEncoder().encodeToString(bgImg));
        os.flush();
        os.close();
        val.setImageDetails(imgs);

        //抠出来的小块拼图相对于整个图片的垂直高度
        val.setOtlY(otlY-circleRadius);

        String sessionId = String.valueOf(SnowFlakeIdUtil.generateID());
        Map<String,Object> paramsMap = new HashMap<>();
        paramsMap.put("val",val);
        paramsMap.put("sessionId",sessionId);

        //将抠图相于于整个图片的水平位移存入缓存
        redisTemplate.opsForValue().set(sessionId,otlX-circleRadius,codeExpire,TimeUnit.SECONDS);

        return paramsMap;
    }

    public boolean checkSwipeCode(float distance,String sessionId){
        Float cacheDistance = (Float) redisTemplate.opsForValue().get(sessionId);
        if(cacheDistance!= null && Math.abs(distance-cacheDistance) <= 5f){
            return true;
        }
        return false;
    }
}
