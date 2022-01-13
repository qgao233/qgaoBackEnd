package com.qgao.springcloud.shiro.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailToken implements AuthenticationToken {

    private static final long serialVersionUID = 891807147782952643L;


    private String userMail;
    private String password;


    /*
    redis里认证信息的key
    前台传的token和这个进行比较，然后取对应的credentials
     */
    @Override
    public Object getPrincipal() {
        return this.userMail;//这里虽然写了，但实际上不会用这个token的信息来进行比较，而是用另一个token
    }

    /*
    redis里认证信息的value
     */
    @Override
    public Object getCredentials() {
        return this.password;
    }

}
