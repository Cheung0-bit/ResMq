package io.github.resmq.dashboard.controller;

import io.github.resmq.dashboard.service.DeadMessageService;
import io.github.resmq.dashboard.service.TopicsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/4/9 12:16
 */
@Controller
@RequestMapping("/deadMessage")
public class DeadMessageController {

    @Resource
    private TopicsService topicsService;

    @Resource
    private DeadMessageService deadMessageService;

    @RequestMapping("")
    public String index(Model model) {
        Map<String, Object> allTopicsInfo = topicsService.getAllTopicsInfo(1, Integer.MAX_VALUE, "");
        model.addAttribute("topicList", allTopicsInfo.get("data"));
        return "deadMessage/deadMessage.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        @RequestParam("topic") String topic) {
        return deadMessageService.getDeadMessages(start, length, topic);
    }

}
