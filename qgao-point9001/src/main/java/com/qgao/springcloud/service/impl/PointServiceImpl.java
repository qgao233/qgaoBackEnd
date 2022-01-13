package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.PointDao;
import com.qgao.springcloud.dto.LevelPointDto;
import com.qgao.springcloud.service.PointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class PointServiceImpl implements PointService,BeanFactoryAware {

    @Resource
    private PointDao pointDao;

    private BeanFactory beanFactory;

    /*
    查询用户当前等级对应的积分，增加积分后，和下一等级需要的积分对比，判断是否需要升级
    todo 如果要集群，分布式锁
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void increaseUserPoint(Long userId, Integer point) throws Exception {
        log.debug("increase user point");
        ReentrantLock lock = new ReentrantLock();

        lock.lock();
        LevelPointDto levelPointDto = pointDao.queryUserLevelAndPoint(userId);
        //用户已有的积分
        int previousPoint = levelPointDto.getPoint();
        //增加积分
        previousPoint += point;
        pointDao.updateUserPoint(userId,previousPoint);
        lock.unlock();

        int level = levelPointDto.getLevel();
        //查询下一等级所需积分
        PointService thisPointService = (PointService)beanFactory.getBean("pointServiceImpl");
        Integer needPoint = thisPointService.getLevelAndPoint().get(level+1);
        if(previousPoint >= needPoint && needPoint != null){//如果超出了等级上限，则needPoint为null
            //todo 发送消息到mq，让通知服务提醒用户升级
            log.debug("提醒用户升级");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void decreaseUserPoint(Long userId, Integer point) throws Exception {
        log.debug("decrease user point");
        ReentrantLock lock = new ReentrantLock();

        lock.lock();
        LevelPointDto levelPointDto = pointDao.queryUserLevelAndPoint(userId);
        //用户已有的积分
        int previousPoint = levelPointDto.getPoint();
        if(previousPoint <= 0)
            throw new RuntimeException(userId+"'s point is not enough.");
        //减少积分
        previousPoint -= point;
        if(previousPoint <= 0)
            throw new RuntimeException(userId+"'s point is not enough.");
        pointDao.updateUserPoint(userId,previousPoint);
        lock.unlock();

        int level = levelPointDto.getLevel();
        //查询下一等级所需积分
        PointService thisPointService = (PointService)beanFactory.getBean("pointServiceImpl");
        Integer needPoint = thisPointService.getLevelAndPoint().get(level+1);
        if(previousPoint >= needPoint && needPoint != null){//如果超出了等级上限，则needPoint为null
            //todo 发送消息到mq，让通知服务提醒用户升级
            log.debug("提醒用户升级");
        }
    }


    /*
    todo 缓存
     */
    @Override
//    @Cacheable(value = "levelPoint")
    public Map<Integer,Integer> getLevelAndPoint() throws Exception{
        List<LevelPointDto> levelPointDtoList = pointDao.queryLevelPoint();
        Map<Integer,Integer> paramsMap = new HashMap<>();
        for(LevelPointDto levelPointDto:levelPointDtoList){
            paramsMap.put(levelPointDto.getLevel(),levelPointDto.getPoint());
        }
        return paramsMap;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void transferUserPoint(Long fromUserId, Long toUserId, Integer point) throws Exception {
        PointService thisPointService = (PointService)beanFactory.getBean("pointServiceImpl");
        thisPointService.decreaseUserPoint(fromUserId,point);
        thisPointService.increaseUserPoint(toUserId,point);
    }

    @Override
    @Cacheable(value = "你他丫的")
    public String test() throws Exception{
        return "我是你大爷";
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
