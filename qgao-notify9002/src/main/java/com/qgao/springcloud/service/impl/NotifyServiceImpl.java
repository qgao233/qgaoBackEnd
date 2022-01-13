package com.qgao.springcloud.service.impl;

import com.qgao.springcloud.dao.UserNotifyDao;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.utils.entity.UserNotify;
import com.qgao.springcloud.handler.OnlineWebSocketHandler;
import com.qgao.springcloud.service.NotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NotifyServiceImpl implements NotifyService,BeanFactoryAware {

    @Resource
    private OnlineWebSocketHandler onlineWebSocketHandler;

    @Resource
    private UserNotifyDao userNotifyDao;

    private BeanFactory beanFactory;

    /*
    获取不在线时间段内产生的通知，并推送
     */
    @Override
    public void getAndSendNotify(Long userId) {
        log.debug("retrieve notify and send notify.");

        List<UserNotify> userNotifies = userNotifyDao.queryByNotifyUserId(userId);
        if(userNotifies == null || userNotifies.size()==0) return;

        //推送通知
        if(onlineWebSocketHandler.sendMessage(userId,new CommonResult<>(200,"get notify success",userNotifies))){
            //通知之后，要删除记录
            NotifyService thisNotifyService = (NotifyService) beanFactory.getBean("notifyServiceImpl");
            thisNotifyService.removeByNotifyUserId(userId);
        }

    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeByNotifyUserId(Long userId) {
        log.debug("remove notify.");
        userNotifyDao.deleteByNotifyUserId(userId);
    }

    /*
    有新通知产生了，赶紧通知在线的用户中的粉丝
     */
    @Override
    public void getFanAndSendNotify(UserNotify userNotify) {
        log.debug("fan notify.");

        Long idolUserId = userNotify.getNotifyFromUserId();
        List<Long> fanUserIds = ((NotifyService) beanFactory.getBean("notifyServiceImpl")).getFanUser(idolUserId);

        //推送通知,返回不在线的粉丝
        List<Long> offlineUserIds = onlineWebSocketHandler.fanMessage(fanUserIds,new CommonResult(200,"new notify",userNotify));

        if(offlineUserIds == null || offlineUserIds.size()==0) return;

        //把不在线的通知保存起来，等上线再通知
        List<UserNotify> userNotifies = new ArrayList<>(offlineUserIds.size());
        UserNotify userNotifyNew;
        for(Long toUserId : offlineUserIds){
            userNotifyNew = new UserNotify(userNotify.getNotifyType(),toUserId,userNotify.getNotifyFromUserId(),userNotify.getNotifyContent(),userNotify.getNotifyDate());
            userNotifies.add(userNotifyNew);
        }

        userNotifyDao.insertBatchNotify(userNotifies);

    }

    @Override
    public List<Long> getFanUser(Long idolUserId) {
        return userNotifyDao.queryFanByIdolUserId(idolUserId);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
