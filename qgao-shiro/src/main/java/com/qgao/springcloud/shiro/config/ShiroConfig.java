package com.qgao.springcloud.shiro.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import com.qgao.springcloud.shiro.filter.MyAuthenticatingFilter;
import com.qgao.springcloud.shiro.serializer.JsonSerializer;
import lombok.Data;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.Filter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

//@ConfigurationProperties(prefix = "server")
//@PropertySource("classpath:redis.properties")
@Configuration
public class ShiroConfig {

    @Value("${qgao.redis.host}")
    private String host;
    @Value("${qgao.redis.timeout}")
    private int timeout;
    @Value("${qgao.redis.expire}")
    private int expire;

//    @Value("${qgao.redis.host}")
//    private static String host;
////    @Value("${qgao.redis.timeout}")
//    private static int timeout;
////    @Value("${qgao.redis.expire}")
//    private static int expire;
//
//    static {
//        InputStream inputStream = null;
//        try {
//            inputStream = new ClassPathResource("redis.properties").getInputStream();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        Properties imgProps = new Properties();
//        try {
//            imgProps.load(inputStream);
//            inputStream.close();
//            host = imgProps.getProperty("qgao.redis.host");
//            timeout = Integer.parseInt(imgProps.getProperty("qgao.redis.timeout"));
//            expire = Integer.parseInt(imgProps.getProperty("qgao.redis.expire"));
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        //???filter?????????????????????
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filter = new HashMap<>();
        filter.put("myAuthc", new MyAuthenticatingFilter());
        //??????????????????
        shiroFilterFactoryBean.setFilters(filter);
        //????????????????????????,?????????????????????????????????????????????
        Map<String, String> filterMap = new LinkedHashMap<>();
////        filterMap.put("/test", "anon");
        filterMap.put("/authc/**", "myAuthc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        return shiroFilterFactoryBean;
    }

    /**
     * ??????shiro redisManager
     * <p>
     * ????????????shiro-redis????????????
     *
     * @return
     */
    @Bean
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setTimeout(timeout);
        return redisManager;
    }


    /**
     * cacheManager ?????? redis??????
     * <p>
     * ????????????shiro-redis????????????
     *
     * @return
     */
    @Bean
    public RedisCacheManager shiroCacheManager(RedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        redisCacheManager.setExpire(expire);

        // ?????? FastJson ???????????????????????????redis ??? value??????
        JsonSerializer serializer = new JsonSerializer(Object.class);
        redisCacheManager.setKeySerializer(serializer);
        redisCacheManager.setValueSerializer(serializer);
        return redisCacheManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao???????????? ??????redis
     * <p>
     * ????????????shiro-redis????????????
     */
    @Bean
    public RedisSessionDAO redisSessionDAO(RedisManager redisManager) {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager);
//      Custom your redis key prefix for session management, if you doesn't define this parameter,
//      shiro-redis will use 'shiro_redis_session:' as default prefix
//      redisSessionDAO.setKeyPrefix("");
        return redisSessionDAO;
    }


    //2.?????????????????????
    @Bean("securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(
            @Qualifier("mailRealm") Realm mailRealm
            ,@Qualifier("userIdRealm") Realm userIdRealm
            , SessionManager sessionManager
            ,RedisCacheManager shiroCacheManager) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //????????????????????????realm
        List<Realm> realms = new ArrayList<>(2);
        realms.add(mailRealm);
        realms.add(userIdRealm);
        defaultWebSecurityManager.setRealms(realms);
        //????????????????????????sessionManager
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setCacheManager(shiroCacheManager);
        return defaultWebSecurityManager;
    }

    //4.??????sessionManager
    @Bean("sessionManager")
    public DefaultWebSessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        //??????session????????????3600s
        defaultWebSessionManager.setGlobalSessionTimeout(3600000L);
        defaultWebSessionManager.setSessionDAO(redisSessionDAO);
        return defaultWebSessionManager;
    }

    /*
    ?????????????????????springboot???????????????404????????????????????????bean???????????????????????????????????????
     */
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){

        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);

        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * ??????static,?????????@Value???yml????????????
     * Shiro?????????????????????:
     * ??????????????????Initializable?????????Shiro bean??????????????????Initializable????????????(??????:UserRealm)
     * ????????????Destroyable?????????Shiro bean??????????????? Destroyable????????????(??????:DefaultSecurityManager)
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * ??????shrio???????????????????????????AOP????????????????????????
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);

        return authorizationAttributeSourceAdvisor;
    }
}
