package com.qgao.springcloud.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebMvc
public class MyWebMvcConfig implements WebMvcConfigurer{

//    @Value("${article.img.url}")
//    private String articleImgUrl;
//    @Value("${article.img.path}")
//    private String articleImgPath;

    //跨域配置(第1种方法)
//    @Bean
//    public CorsFilter corsFilter()
//    {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        // 是否允许请求带有验证信息
//        config.setAllowCredentials(true);
//
//        // 允许访问的客户端域名
//        // (springboot2.4以上的加入这一段可解决 allowedOrigins cannot contain the special value "*"问题)
//        List<String> allowedOriginPatterns = new ArrayList<>();
//        allowedOriginPatterns.add("*");
//        config.setAllowedOriginPatterns(allowedOriginPatterns);
//
//        // 设置访问源地址
////         config.addAllowedOrigin("http://127.0.0.1:8020");
//        // 设置访问源请求头
//        config.addAllowedHeader("*");
//        // 设置访问源请求方法
//        config.addAllowedMethod("*");
//        // 对接口配置跨域设置
//        source.registerCorsConfiguration("/**", config);
//        return new CorsFilter(source);
//    }

    //跨域配置(第2种方法)
    //"跨域资源共享"(Cross-origin resource sharing)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //对那些请求路径进行跨域处理
        registry.addMapping("/**")
                // 支持的域
//                .allowedOrigins("*")//设置了credential=true后，origin为*则无法正常工作
                .allowedOriginPatterns("*")
                .allowCredentials(true)
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                // 探测请求有效时间，单位秒
                .maxAge(3600);
    }

    //静态资源访问
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//            registry.addResourceHandler(articleImgUrl+"/**")
//                    .addResourceLocations(articleImgPath);
//
//    }

}
