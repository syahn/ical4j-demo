package com.calendar.controller;

import com.calendar.service.ICalService;
import com.calendar.service.JsoupService;
import com.calendar.service.PrintConverterService;
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

    private static int startMonth;
    private static int endMonth;
    private JsoupService jSoup;
    private ICalService iCal;
    private PrintConverterService converter;
    private String tempUrl;
    private String month;

    @Autowired
    public PrintController(JsoupService jSoup, ICalService iCal, PrintConverterService converter) {
        this.iCal = iCal;
        this.jSoup = jSoup;
        this.converter = converter;
    }

    @ResponseBody
    @PostMapping("/save-url")
    public void saveUrl(
            @RequestParam("previewUrl") String preview,
            @RequestParam("month") String monthVal
    ) {
        tempUrl = preview;
        month = monthVal;
    }

    @ResponseBody
    @PostMapping("/print-change-range")
    public void savePrintRange(
            @RequestParam("start") String start,
            @RequestParam("end") String end
    ) {
        startMonth = Integer.parseInt(start);
        endMonth = Integer.parseInt(end);
    }

    @ResponseBody
    @PostMapping("/make-preview")
    public void saveUrl(
    ) throws ParseException, ParserException, IOException {
        jSoup.makeHTMLfiles(startMonth,endMonth);
    }

    @RequestMapping("/preview")
    public String viewPreview(Model model) throws ParseException, ParserException, IOException {

        model.addAttribute("previewurl", tempUrl);
        model.addAttribute("month", month);

        jSoup.makeHTMLfiles(startMonth, endMonth);

        return "preview";
    }

    //converter for pdf save and print
    @PostMapping("/convert")
    public String convert(
            @RequestParam("startMonth") int startMonth,
            @RequestParam("endMonth") int endMonth,
            @RequestParam("orientation") int orientation
    ) throws ParseException, ParserException, IOException, InterruptedException {
        //converting html to pdf - by url
        jSoup.makeHTMLfiles(startMonth, endMonth);
        converter.makeAPdf(startMonth, endMonth, orientation);

        return "preview";
    }
}
