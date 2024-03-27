package io.github.resmq.dashboard.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/3/27 18:05
 */
@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) {

//        Map<String, Object> dashboardMap = xxlMqMessageService.dashboardInfo();
//        model.addAllAttributes(dashboardMap);

        return "index";
    }


}
