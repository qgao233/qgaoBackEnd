package com.qgao.springcloud.utils.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieUtil {

    public static Cookie getTokenOfCookie(HttpServletRequest request){
        for(Cookie cookie : request.getCookies()){
            if(cookie.getName().equals("token")){
                return cookie;
            }
        }
        return null;
    }
}
