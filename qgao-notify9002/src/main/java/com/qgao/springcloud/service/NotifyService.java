package com.qgao.springcloud.service;

import com.qgao.springcloud.utils.entity.UserNotify;

import java.util.List;

public interface NotifyService {

    void getAndSendNotify(Long userId);

    void removeByNotifyUserId(Long userId);

    void getFanAndSendNotify(UserNotify userNotify);

    List<Long> getFanUser(Long idolUserId);
}
