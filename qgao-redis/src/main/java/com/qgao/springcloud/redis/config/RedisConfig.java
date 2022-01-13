package com.qgao.springcloud.redis.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.expression.PropertyAccessor;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    // 倘若 spring.redis.host 不存在，则会默认为127.0.0.1.
    @Value("${spring.redis.host:#{'127.0.0.1'}}")
    private String hostName;

    @Value("${spring.redis.port:#{6379}}")
    private int port;

    @Value("${spring.redis.password:#{123456}}")
    private String password;

    @Value("${spring.redis.timeout:#{3000}}")
    private int timeout;

    @Value("${spring.redis.lettuce.pool.max-idle:#{16}}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle:#{1}}")
    private int minIdle;

    @Value("${spring.redis.lettuce.pool.max-wait:#{16}}")
    private long maxWaitMillis;

    @Value("${spring.redis.lettuce.pool.max-active:#{16}}")
    private int maxActive;

    @Value("${spring.redis.database:#{0}}")
    private int databaseId;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {

        RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(
                hostName, port
        );

        // 设置选用的数据库号码
        ((RedisStandaloneConfiguration) redisConfiguration).setDatabase(databaseId);

        // 设置 redis 数据库密码
//        ((RedisStandaloneConfiguration) redisConfiguration).setPassword(password);

        // 连接池配置
        GenericObjectPoolConfig<Object> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxWaitMillis(maxWaitMillis);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder
                = LettucePoolingClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(timeout));

        LettucePoolingClientConfiguration lettucePoolingClientConfiguration = builder.build();

        builder.poolConfig(poolConfig);

        // 根据配置和客户端配置创建连接
        return new LettuceConnectionFactory(redisConfiguration, lettucePoolingClientConfiguration);
    }

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(
            LettuceConnectionFactory lettuceConnectionFactory) {

        RedisTemplate<String, Serializable> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 使用 FastJsonRedisSerializer 来序列化和反序列化redis 的 value的值
        FastJsonRedisSerializer<Object> serializer = new FastJsonRedisSerializer<>(Object.class);
        //默认autoType全部开放（实际项目中最好指定规定包，提高效率）
        ParserConfig.getGlobalInstance().setAsmEnable(true);
        // 建议使用这种方式，小范围指定白名单
//        ParserConfig.getGlobalInstance().addAccept("com.qgao");
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        serializer.setFastJsonConfig(fastJsonConfig);
//        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);
        template.setConnectionFactory(lettuceConnectionFactory);
        return template;
    }

    /*
    使@Cacheable存到redis的值可以用自定义的序列化方式，使数据可视化
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String, Serializable> redisTemplate) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()));
        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
    }

}
