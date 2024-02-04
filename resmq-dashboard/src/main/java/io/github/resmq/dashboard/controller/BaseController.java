package io.github.resmq.dashboard.controller;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.*;
import io.github.resmq.dashboard.service.*;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * <基页>
 *
 * @author zhanglin
 * @createTime 2024/1/31 14:21
 */
@Controller
public class BaseController {

    @Resource
    private IndexParamService indexParamService;

    @Resource
    private TopicsService topicsService;

    @Resource
    private ScheduledService scheduledService;

    @Resource
    private DeadMessageService deadMessageService;

    @Resource
    private PendingService pendingService;

    @Value("${server.port:8080}")
    private int port;

    @GetMapping
    public ModelAndView index() {
        ModelAndView index = new ModelAndView("index");
        addBasicParam(index);
        ResMqProperties resMqProperties = indexParamService.getResMqProperties();
        index.addObject("resMqProperties", resMqProperties);
        return index;
    }

    @GetMapping("topics")
    public ModelAndView topics() {
        ModelAndView topics = new ModelAndView("topics");
        addBasicParam(topics);
        List<TopicInfo> topicsInfo = topicsService.getAllTopicsInfo();
        topics.addObject("topicsInfo", topicsInfo);
        return topics;
    }

    @GetMapping("topics/{topic}")
    public ModelAndView topicDetail(@PathVariable("topic") String topic) {
        ModelAndView topicDetail = new ModelAndView("group");
        addBasicParam(topicDetail);
        TopicInfo topicInfo = topicsService.getTopicInfo(Constants.TOPIC_PREFIX + topic);
        List<GroupInfo> groupInfos = topicsService.getTopicDetail(topic);
        topicDetail.addObject("topicInfo", topicInfo);
        topicDetail.addObject("groupInfos", groupInfos);
        return topicDetail;
    }

    @GetMapping("scheduled")
    public ModelAndView scheduled() {
        ModelAndView scheduled = new ModelAndView("scheduled");
        addBasicParam(scheduled);
        List<ScheduledInfo> scheduledMessages = scheduledService.getScheduledMessages();
        scheduled.addObject("scheduledMessages", scheduledMessages);
        return scheduled;
    }

    @GetMapping("scheduled/{key}")
    public ModelAndView scheduledTasks(@PathVariable("key") String key) {
        ModelAndView scheduledTask = new ModelAndView("scheduled-task");
        addBasicParam(scheduledTask);
        List<ScheduledTask> scheduledTasks = scheduledService.getScheduledTasks(key);
        scheduledTask.addObject("scheduledTasks", scheduledTasks);
        scheduledTask.addObject("delayTopic", key);
        return scheduledTask;
    }

    @GetMapping("dead")
    public ModelAndView dead() {
        ModelAndView dead = new ModelAndView("dead");
        addBasicParam(dead);
        List<CommonMessageSummary> dlq = deadMessageService.getDMSummary();
        dead.addObject("dlq", dlq);
        return dead;
    }

    @GetMapping("pending")
    public ModelAndView pending() {
        ModelAndView pending = new ModelAndView("pending");
        addBasicParam(pending);
        List<CommonMessageSummary> pdm = pendingService.getPendingSummary();
        pending.addObject("pdm", pdm);
        return pending;
    }

    private void addBasicParam(ModelAndView modelAndView) {
        String urlPrefix = "http://localhost:" + port;
        modelAndView.addObject("urlPrefix", urlPrefix);
    }

}
