package com.calendar.controller;

import net.fortuna.ical4j.data.ParserException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

/**
 * Created by NAVER on 2017-07-14.
 */
@Controller
@EnableAutoConfiguration
public class ICalController {

    @GetMapping("/")
    public String index() throws IOException, ParserException {
        return "index";
    }

    @GetMapping("/login")
    public String login() throws IOException, ParserException {
        return "login";
    }

    @RequestMapping(value="/month/{id}")
    public String renderMonthView(@PathVariable int id){
        return "/month_view/month_" + Integer.toString(id);
    }
}