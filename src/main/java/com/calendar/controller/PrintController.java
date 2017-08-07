package com.calendar.controller;

import com.calendar.service.JsoupService;
import com.calendar.service.PrintConverterService;
import com.calendar.service.SettingService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.text.ParseException;

/**
 * Created by NAVER on 2017-07-25.
 */
@Controller
@EnableAutoConfiguration
public class PrintController {

    private JsoupService jSoup;
    private PrintConverterService converter;
    private SettingService setting;

    @Autowired
    public PrintController(
            JsoupService jSoup,
            PrintConverterService converter,
            SettingService setting
    ) {
        this.jSoup = jSoup;
        this.converter = converter;
        this.setting = setting;
    }

    @ResponseBody
    @PostMapping("/save-url")
    public void saveUrl(
            @RequestParam("previewUrl") String preview,
            @RequestParam("month") String monthVal
    ) {
        setting.setTempUrl(preview);
        setting.setCurrentMonth(Integer.parseInt(monthVal));
    }

    @ResponseBody
    @PostMapping("/make-preview")
    public void saveUrl(@RequestParam("month") int monthVal) throws ParseException, ParserException, IOException {
//        System.out.println(setting.getCurrentMonth());
        setting.setCurrentMonth(monthVal);
        int startMonth = setting.getStartMonth();
        int endMonth = setting.getEndMonth();

        if (startMonth == 0 && endMonth == 0) {
            System.out.println("it's first");
            setting.setStartMonth(monthVal);
            setting.setEndMonth(monthVal);
        } else {
            System.out.println("it's second");
            System.out.printf("%d, %d\n", setting.getStartMonth(), setting.getEndMonth());
        }


        jSoup.makeHTMLfiles();
    }

    @RequestMapping("/preview")
    public String viewPreview(Model model) throws ParseException, ParserException, IOException {

        String tempUrl = setting.getTempUrl();
        String month = Integer.toString(setting.getCurrentMonth());

        model.addAttribute("previewurl", tempUrl);
        model.addAttribute("month", month);

        jSoup.makeHTMLfiles();

        return "preview";
    }

    @ResponseBody
    @PostMapping("/print-change-range")
    public void changeRange(@RequestParam("start") int start,
                            @RequestParam("end") int end) {
        setting.setStartMonth(start);
        setting.setEndMonth(end);
//        System.out.println("yolo");
    }

    //converter for pdf save and print
    @PostMapping("/convert")
    public String convert(
            @RequestParam("startMonth") int startMonth,
            @RequestParam("endMonth") int endMonth,
            @RequestParam("orientation") int orientation
    ) throws ParseException, ParserException, IOException, InterruptedException {
        //converting html to pdf - by url
        setting.setStartMonth(startMonth);
        setting.setEndMonth(endMonth);
        jSoup.makeHTMLfiles();
        converter.makeAPdf(startMonth, endMonth, orientation);

        return "preview";
    }
}
