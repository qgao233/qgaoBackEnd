package com.qgao.springcloud.helper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;

@Component
@Slf4j
public class CookieHelper {

    //保存在浏览器本地的cookie会对应着域名，只有访问cookie对应的域名时，这个cookie才会被携带
    //若是想要cookie保存多个域名，可以生成多个相同的cookie，只是域名不同，浏览器访问不同域名时携带不同cookie
    @Value("${qgao.cookie.domain}")
    private String domain;
    //浏览器本地的cookie指定了服务器中布署web项目的路径，"/"表示当前布署路径下的所有web项目都能获得该cookie
    @Value("${qgao.cookie.path}")
    private String path;
    @Value("${qgao.cookie.month}")
    private Integer month;//30天
    @Value("${qgao.cookie.day}")
    private Integer day;


    public void removeCookie(String key,HttpServletResponse response){
        response.setHeader( "Pragma", "no-cache" );
        response.addHeader( "Cache-Control", "must-revalidate" );
        response.addHeader( "Cache-Control", "no-cache" );
        response.addHeader( "Cache-Control", "no-store" );
        response.setDateHeader("Expires", 0);
        Cookie token = new Cookie(key, "");
        token.setDomain(domain);
        token.setPath(path);
        token.setMaxAge(0);
        response.addCookie(token);
    }

    public void addCookie(Map<String,Object> paramsMap, boolean isRemember
            , HttpServletRequest request
            , HttpServletResponse response){
        //下三行是跨域配置,已经用springMVC做了跨域处理，这里就不用写了
//        String origin = request.getHeader("Origin");
//        response.setHeader("Access-Control-Allow-Origin", origin);
//        response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, If-Modified-Since");
//        response.addHeader("Access-Control-Allow-Credentials", "true");


        //由于samesite的问题，跨域时必须设置，而servlet的cookie类暂不支持设置该属性，
        // 因此通过spring提供的responsecookie实现

//        Cookie token = new Cookie((String)paramsMap.get("key"), (String)paramsMap.get("value"));
//        token.setDomain(domain);
//        token.setPath(path);
//        token.setMaxAge(day);
//        if(isRemember){
//            token.setMaxAge(month);
//        }


//        response.addCookie(token);

        ResponseCookie.ResponseCookieBuilder responseCookieBuilder = ResponseCookie.from((String)paramsMap.get("key"), (String)paramsMap.get("value")) // key & value
                .httpOnly(true)		// 禁止js读取
                .secure(false)		// 在http下也传输
                .domain(domain)// 域名
                .path(path)			// path
                .sameSite("Lax");	// 若是Lax,则大多数情况也是不发送第三方 Cookie，但是导航到目标网址的 Get 请求除外
        responseCookieBuilder.maxAge(day);
        if(isRemember){
            responseCookieBuilder.maxAge(month);
        }
        ResponseCookie cookie = responseCookieBuilder.build();

        // 设置Cookie Header
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
