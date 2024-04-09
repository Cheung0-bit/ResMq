package io.github.resmq.dashboard.controller;

import io.github.resmq.dashboard.service.ScheduledService;
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
 * @createTime 2024/4/8 13:49
 */
@Controller
@RequestMapping("/delayMessage")
public class ScheduledMessageController {

    @Resource
    private ScheduledService scheduledService;

    @RequestMapping("")
    public String index(Model model) {

        return "delayMessage/delayMessage.index";
    }

    @RequestMapping("/pageList")
    @ResponseBody
    public Map<String, Object> pageList(@RequestParam(required = false, defaultValue = "0") int start,
                                        @RequestParam(required = false, defaultValue = "10") int length,
                                        @RequestParam("scheduledName") String scheduledName) {
        return scheduledService.returnScheduledTasks(start, length, scheduledName);
    }

}
