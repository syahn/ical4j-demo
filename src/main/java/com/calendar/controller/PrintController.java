package com.calendar.controller;

import com.calendar.service.JsoupService;
import com.calendar.service.PrintConverterService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    public PrintController(
            JsoupService jSoup,
            PrintConverterService converter
    ) {
        this.jSoup = jSoup;
        this.converter = converter;
    }

    @ResponseBody
    @PostMapping("/make-preview")
    public void makePreview(@RequestParam("startMonth") int startMonth, @RequestParam("endMonth") int endMonth, @RequestParam("currentYear") int currentYear) throws ParseException, ParserException, IOException {

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear);

    }

    @PostMapping("/preview")
    public String viewPreviewWindow(Model model, @RequestParam("month") int month, @RequestParam("year") int year) throws ParseException, ParserException, IOException {

        long millis = System.currentTimeMillis();
        String sig = Long.toHexString(millis);
        System.out.println(sig);

        model.addAttribute("initialMonth",month);
        model.addAttribute("initialYear",year);
        model.addAttribute("fileID",sig);

        return "preview";
    }

    //converter for pdf save and print
    @PostMapping("/convert")
    public String convert(
            @RequestParam("startMonth") int startMonth,
            @RequestParam("endMonth") int endMonth,
            @RequestParam("currentYear") int currentYear,
            @RequestParam("orientation") int orientation
    ) throws ParseException, ParserException, IOException, InterruptedException {
        //converting html to pdf - by url

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear);
        converter.makeAPdf(startMonth, endMonth, orientation);

        return "preview";
    }
}
