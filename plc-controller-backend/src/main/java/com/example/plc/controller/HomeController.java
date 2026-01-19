package com.example.plc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 首页控制器，用于处理根路径请求
 */
@Controller
public class HomeController {

    /**
     * 根路径请求重定向到首页
     * @return 重定向到首页
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
}
