package com.calendar.controller;

import net.fortuna.ical4j.data.ParserException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Created by NAVER on 2017-07-14.
 */
@Controller
@EnableAutoConfiguration
public class iCalController {

    @GetMapping("/")
    public String index() throws IOException, ParserException {
        return "index";
    }

    @RequestMapping(value="/month_6")
    public String month_6(){

        return "month_6";
    }

    @RequestMapping(value = "/month_7")
    public String month_7(){

        return "month_7";
    }

    @RequestMapping(value = "/month_8")
    public String month_8(){
        return "month_8";
    }



}