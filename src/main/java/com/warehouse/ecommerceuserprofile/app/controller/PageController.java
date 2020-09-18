package com.warehouse.ecommerceuserprofile.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class PageController {
    @RequestMapping("/")
    public String index(){
        return "index";
    }

    @RequestMapping("/tags")
    public String tags(){
        return "tags";
    }

}