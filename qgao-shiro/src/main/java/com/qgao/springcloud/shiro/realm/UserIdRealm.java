package com.qgao.springcloud.shiro.realm;

import com.qgao.springcloud.shiro.service.ValidateService;
import com.qgao.springcloud.shiro.token.UserIdToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("userIdRealm")
public class UserIdRealm extends AuthorizingRealm {

    @Resource
    private ValidateService validateService;

    //重写父类方法，判断用户使用的token类型，该realm是否能够处理
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UserIdToken;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Long userId = (Long) principalCollection.getPrimaryPrincipal();

        try {
            Map<String, Object> paramsMap = validateService.getUserLevelPerm(userId);

            SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
            simpleAuthorizationInfo.addRole(String.valueOf(paramsMap.get("level")));
            simpleAuthorizationInfo.addStringPermissions((List<String>)paramsMap.get("permissions"));

            return simpleAuthorizationInfo;
        } catch (Exception e) {
            log.error("get permissions failure");
            return null;
        }
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UserIdToken mailToken = (UserIdToken) authenticationToken;

        Long userId = mailToken.getUserId();
        String md5Pwd = mailToken.getPassword();
        //这里查数据库，并返回userId
        try {
            Long tmpUserId = validateService.checkUserLoginByUserId(userId,md5Pwd);
            if(tmpUserId == null) return null;
            validateService.updateUserLoginTime(userId);

            return new SimpleAuthenticationInfo(tmpUserId,md5Pwd,getName());
        } catch (Exception e) {
            log.error("login by userId failure");
            return null;
        }
    }
}
