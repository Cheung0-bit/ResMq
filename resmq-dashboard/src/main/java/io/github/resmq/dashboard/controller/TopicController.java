package io.github.resmq.dashboard.controller;

import io.github.resmq.dashboard.entity.GroupInfo;
import io.github.resmq.dashboard.model.ReturnT;
import io.github.resmq.dashboard.service.TopicsService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/3/29 21:21
 */
@Controller
@RequestMapping("/topic")
public class TopicController {

    @Resource
    private TopicsService topicsService;

    @RequestMapping("")
    public String index(Model model) {

        return "topic/topic.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        @RequestParam("topic") String topic) {
        return topicsService.getAllTopicsInfo(start, length, topic);
    }

    @RequestMapping("/groupInfos")
    @ResponseBody
    public ReturnT<String> groupsInfos(@RequestParam("topic") String topic) {
        return ReturnT.success(topicsService.topicDetailHtml(topic));
    }

}
