package io.github.resmq.dashboard.controller;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.dashboard.annotation.PermissionLimit;
import io.github.resmq.dashboard.intercepter.PermissionInterceptor;
import io.github.resmq.dashboard.model.ReturnT;
import io.github.resmq.dashboard.service.IndexParamService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.Map;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/3/27 18:05
 */
@Controller
public class IndexController {

    @Resource
    private IndexParamService indexParamService;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {
        ResMqProperties resMqProperties = indexParamService.getResMqProperties();
        model.addAttribute("resMqProperties", resMqProperties);
        Map<String, Integer> map = indexParamService.getCount();
        model.addAllAttributes(map);
        return "index";
    }

    @RequestMapping("/chartInfo")
    @ResponseBody
    public ReturnT<Map<String, Object>> chartInfo(Date startDate, Date endDate) {
//        ReturnT<Map<String, Object>> chartInfo = xxlMqMessageService.chartInfo(startDate, endDate);
//        return chartInfo;
        return null;
    }

    @RequestMapping("/toLogin")
    @PermissionLimit(limit = false)
    public String toLogin(Model model, HttpServletRequest request) {
        if (PermissionInterceptor.ifLogin(request)) {
            return "redirect:/";
        }
        return "login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> loginDo(HttpServletRequest request, HttpServletResponse response, String userName, String password, String ifRemember) {
        // valid
        if (PermissionInterceptor.ifLogin(request)) {
            return ReturnT.SUCCESS;
        }
        // param
        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return new ReturnT<>(500, "请输入账号密码");
        }
        boolean ifRem = "on".equals(ifRemember);

        // do login
        boolean loginRet = PermissionInterceptor.login(response, userName, password, ifRem);
        if (!loginRet) {
            return new ReturnT<>(500, "账号密码错误");
        }
        return ReturnT.SUCCESS;
    }

    @RequestMapping("/help")
    public String help() {
        return "help";
    }

    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    @PermissionLimit(limit = false)
    public ReturnT<String> logout(HttpServletRequest request, HttpServletResponse response) {
        if (PermissionInterceptor.ifLogin(request)) {
            PermissionInterceptor.logout(request, response);
        }
        return ReturnT.SUCCESS;
    }

}
