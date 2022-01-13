package com.qgao.springcloud.shiro.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.shiro.authc.AuthenticationToken;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserIdToken implements AuthenticationToken {

    private static final long serialVersionUID = 891807147782952643L;

    private Long userId;
    private String password;


    @Override
    public Object getPrincipal() {
        return this.userId;
    }

    @Override
    public Object getCredentials() {
        return this.password;
    }
}
