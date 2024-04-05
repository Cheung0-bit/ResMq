package io.github.resmq.dashboard.controller;

import io.github.resmq.dashboard.service.TopicsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Map;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/3/30 10:51
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    @Resource
    private TopicsService topicsService;

    @RequestMapping("")
    public String index(Model model) {
        Map<String, Object> allTopicsInfo = topicsService.getAllTopicsInfo(1, Integer.MAX_VALUE, "");
        model.addAttribute("topicList", allTopicsInfo.get("data"));
        return "message/message.index";
    }

}
