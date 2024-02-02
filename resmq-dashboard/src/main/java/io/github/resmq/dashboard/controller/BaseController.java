package io.github.resmq.dashboard.controller;

import io.github.resmq.core.config.ResMqProperties;
import io.github.resmq.core.constant.Constants;
import io.github.resmq.dashboard.entity.GroupInfo;
import io.github.resmq.dashboard.entity.TopicInfo;
import io.github.resmq.dashboard.service.IndexParamService;
import io.github.resmq.dashboard.service.TopicsService;
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

    private void addBasicParam(ModelAndView modelAndView) {
        String urlPrefix = "http://localhost:" + port;
        modelAndView.addObject("urlPrefix", urlPrefix);
    }

}
