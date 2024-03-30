package io.github.resmq.dashboard.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/3/30 10:51
 */
@Controller
@RequestMapping("/message")
public class MessageController {

    @RequestMapping("")
    public String index(Model model) {

        return "message/message.index";
    }

}
