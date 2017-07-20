package com.calendar.controller;

import net.fortuna.ical4j.data.ParserException;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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

    @GetMapping("/month_6")
    public String month() throws IOException, ParserException {
        return "month_6";
    }

    @GetMapping("/month_7")
    public String month2(Model model) throws IOException, ParserException {
        model.addAttribute("month", 7);
        return "month_7";
    }
}