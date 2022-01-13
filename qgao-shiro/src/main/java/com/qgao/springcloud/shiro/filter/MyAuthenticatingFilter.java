package com.qgao.springcloud.shiro.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qgao.springcloud.utils.entity.CommonResult;
import com.qgao.springcloud.shiro.token.UserIdToken;
import com.qgao.springcloud.utils.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
public class MyAuthenticatingFilter extends AuthenticatingFilter {

    /*
    处理掉跨域时出现的多余的options请求
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
        httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        String[] tokenArray = getRequestToken(servletRequest).split(StringUtil.COOKIE_SEPARATOR);
        Long userId = Long.parseLong(tokenArray[0]);
        String md5Pwd = tokenArray[1];

        return new UserIdToken(userId,md5Pwd);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {

        //1.检查请求中是否含有token
        String token = getRequestToken(servletRequest);
        //2. 如果客户端没有携带token，拦下请求
        if (null == token || "".equals(token)) {
            log.debug("Token为空，您无权访问该接口");
            responseTokenError(servletResponse, "Token为空，您无权访问该接口");
            return false;
        }
        //3. 如果有，对进行进行token验证
        return executeLogin(servletRequest, servletResponse);
    }

    /*
    尝试认证
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        UserIdToken jwtToken = (UserIdToken) createToken(request, response);
        try {
            SecurityUtils.getSubject().login(jwtToken);
        } catch (AuthenticationException e) {
            log.debug("Token无效，您无权访问该接口");
            responseTokenError(response, "Token无效，您无权访问该接口");
            return false;
        }
        return true;
    }

    /**
     * 获取请求的token
     */
    private String getRequestToken(ServletRequest servletRequest) {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        //从header中获取token
        String token = httpRequest.getHeader("token");

        //没有就从参数中获取token
        if (StringUtils.isEmpty(token)) {
            token = httpRequest.getParameter("token");
        }
        //再不行，从cookie里获取token
        if (StringUtils.isEmpty(token)) {
            Cookie[] cks = httpRequest.getCookies();
            if (cks != null) {
                for (Cookie cookie : cks) {
                    if (cookie.getName().equals("token")) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }
        return token;
    }

    /**
     * 无需转发，直接返回Response信息 Token认证错误
     */
    private void responseTokenError(ServletResponse response, String info) {
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        try (PrintWriter out = httpServletResponse.getWriter()) {
            ObjectMapper objectMapper = new ObjectMapper();
            String data = objectMapper.writeValueAsString(new CommonResult<>(402, info));
            out.append(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
