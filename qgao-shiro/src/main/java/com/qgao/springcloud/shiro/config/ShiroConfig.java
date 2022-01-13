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
        //给filter设置安全管理器
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filter = new HashMap<>();
        filter.put("myAuthc", new MyAuthenticatingFilter());
        //自定义过滤器
        shiroFilterFactoryBean.setFilters(filter);
        //配置系统受限资源,这里选择通过注解的方式进行拦截
        Map<String, String> filterMap = new LinkedHashMap<>();
////        filterMap.put("/test", "anon");
        filterMap.put("/authc/**", "myAuthc");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterMap);

        return shiroFilterFactoryBean;
    }

    /**
     * 配置shiro redisManager
     * <p>
     * 使用的是shiro-redis开源插件
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
     * cacheManager 缓存 redis实现
     * <p>
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public RedisCacheManager shiroCacheManager(RedisManager redisManager) {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager);
        redisCacheManager.setExpire(expire);

        // 使用 FastJson 来序列化和反序列化redis 的 value的值
        JsonSerializer serializer = new JsonSerializer(Object.class);
        redisCacheManager.setKeySerializer(serializer);
        redisCacheManager.setValueSerializer(serializer);
        return redisCacheManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * <p>
     * 使用的是shiro-redis开源插件
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


    //2.创建安全管理器
    @Bean("securityManager")
    public DefaultWebSecurityManager getDefaultWebSecurityManager(
            @Qualifier("mailRealm") Realm mailRealm
            ,@Qualifier("userIdRealm") Realm userIdRealm
            , SessionManager sessionManager
            ,RedisCacheManager shiroCacheManager) {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        //给安全管理器设置realm
        List<Realm> realms = new ArrayList<>(2);
        realms.add(mailRealm);
        realms.add(userIdRealm);
        defaultWebSecurityManager.setRealms(realms);
        //给安全管理器设置sessionManager
        defaultWebSecurityManager.setSessionManager(sessionManager);
        defaultWebSecurityManager.setCacheManager(shiroCacheManager);
        return defaultWebSecurityManager;
    }

    //4.创建sessionManager
    @Bean("sessionManager")
    public DefaultWebSessionManager sessionManager(RedisSessionDAO redisSessionDAO) {
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        //设置session过期时间3600s
        defaultWebSessionManager.setGlobalSessionTimeout(3600000L);
        defaultWebSessionManager.setSessionDAO(redisSessionDAO);
        return defaultWebSessionManager;
    }

    /*
    配置这个，否则springboot访问接口全404（之前不注入这个bean也可以访问，就突然不行了）
     */
    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){

        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);

        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 加上static,可以让@Value从yml中读取值
     * Shiro生命周期处理器:
     * 用于在实现了Initializable接口的Shiro bean初始化时调用Initializable接口回调(例如:UserRealm)
     * 在实现了Destroyable接口的Shiro bean销毁时调用 Destroyable接口回调(例如:DefaultSecurityManager)
     */
    @Bean
    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 启用shrio授权注解拦截方式，AOP式方法级权限检查
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
                new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);

        return authorizationAttributeSourceAdvisor;
    }
}
