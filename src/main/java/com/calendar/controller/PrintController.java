package com.calendar.controller;

import com.calendar.service.PrintConverterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * Created by NAVER on 2017-07-25.
 */
@Controller
@EnableAutoConfiguration
public class PrintController {

    private String tempUrl;
    private String month;

    @Autowired
    private PrintConverterService converter;

    @ResponseBody
    @RequestMapping("/save-url")
    public void saveUrl(
            @RequestParam("previewUrl") String preview,
            @RequestParam("month") String monthVal
    ){

        tempUrl = preview;
        month = monthVal;
    }

    @RequestMapping("/preview")
    public String viewPreview(Model model){

        model.addAttribute("previewurl", tempUrl);
        model.addAttribute("month", month);

        return "month_preview";
    }

    //converter for pdf save and print
    @RequestMapping(value = "/convert", method = RequestMethod.POST)
    public String convert(
            @RequestParam("startMonth") int startMonth,
            @RequestParam("endMonth") int endMonth,
            @RequestParam("orientation") int orientation
    ){
        //converting html to pdf - by url
        try {
            converter.makeAPdf(startMonth, endMonth, orientation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "preview";
    }

}
