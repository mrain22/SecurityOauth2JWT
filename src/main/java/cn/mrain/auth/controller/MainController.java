package cn.mrain.auth.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @RequestMapping("/")
    public String mainPage(){
        return "index";
    }
    @RequestMapping("/test")
    @ResponseBody
    public String testPage(){
        return "登录成功！";
    }
    @RequestMapping("/test2")
    @ResponseBody
    public String test2Page(){
        return "登录成功！";
    }
    @RequestMapping("/user")
    @ResponseBody
    public Object user(Authentication user){
        return user;
    }
}
