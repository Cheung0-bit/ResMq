package io.github.resmq.dashboard.intercepter;

import io.github.resmq.dashboard.annotation.PermissionLimit;
import io.github.resmq.dashboard.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.math.BigInteger;

/**
 * <简易的权限拦截>
 *
 * @Author zhanglin
 * @createTime 2024/3/26 20:52
 */
@Component
public class PermissionInterceptor implements HandlerInterceptor, InitializingBean {

    // ---------------------- init ----------------------

    @Value("${res-mq.login.username}")
    private String username;
    @Value("${res-mq.login.password}")
    private String password;

    public static final String LOGIN_IDENTITY_KEY = "RES_MQ_LOGIN_IDENTITY";
    private static String LOGIN_IDENTITY_TOKEN;

    public static String getLoginIdentityToken() {
        return LOGIN_IDENTITY_TOKEN;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // valid
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new RuntimeException("权限账号密码不可为空");
        }
        // login token
        String tokenTmp = DigestUtils.md5DigestAsHex((username + "_" + password).getBytes());        //.getBytes("UTF-8")
        tokenTmp = new BigInteger(1, tokenTmp.getBytes()).toString(16);

        LOGIN_IDENTITY_TOKEN = tokenTmp;
    }

    public static boolean login(HttpServletResponse response, String username, String password, boolean ifRemember) {
        // login token
        String tokenTmp = DigestUtils.md5DigestAsHex((username + "_" + password).getBytes());
        tokenTmp = new BigInteger(1, tokenTmp.getBytes()).toString(16);

        if (!getLoginIdentityToken().equals(tokenTmp)) {
            return false;
        }

        // do login
        CookieUtil.set(response, LOGIN_IDENTITY_KEY, getLoginIdentityToken(), ifRemember);
        return true;
    }

    public static void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.remove(request, response, LOGIN_IDENTITY_KEY);
    }

    public static boolean ifLogin(HttpServletRequest request) {
        String identityInfo = CookieUtil.getValue(request, LOGIN_IDENTITY_KEY);
        if (identityInfo == null || !getLoginIdentityToken().equals(identityInfo.trim())) {
            return false;
        }
        return true;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!ifLogin(request)) {
            HandlerMethod method = (HandlerMethod) handler;
            PermissionLimit permission = method.getMethodAnnotation(PermissionLimit.class);
            if (permission == null || permission.limit()) {
                response.sendRedirect(request.getContextPath() + "/toLogin");
                return false;
            }
        }
        return true;
    }
}
