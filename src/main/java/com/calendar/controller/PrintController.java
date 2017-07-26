package com.calendar.controller;

import com.calendar.data.ICalFilteredEvent;
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
import java.util.List;

/**
 * Created by NAVER on 2017-07-25.
 */
@Controller
@EnableAutoConfiguration
public class PrintController {

    private JsoupService jSoup;
    private ICalService iCal;

    private String tempUrl;
    private String month;

    private static int startMonth;
    private static int endMonth;

    @Autowired
    public PrintController(JsoupService jSoup, ICalService iCal) {
        this.iCal = iCal;
        this.jSoup = jSoup;
    }

    @Autowired
    private PrintConverterService converter;

    @ResponseBody
    @PostMapping("/save-url")
    public void saveUrl(
            @RequestParam("previewUrl") String preview,
            @RequestParam("month") String monthVal
    ) {
        tempUrl = preview;
        month = monthVal;
    }

    @RequestMapping("/preview")
    public String viewPreview(Model model) throws IOException, ParseException, ParserException {

        model.addAttribute("previewurl", tempUrl);
        model.addAttribute("month", month);

        jSoup.makeHTMLfiles(6, 8);

        return "preview";
    }

    //converter for pdf save and print
    @PostMapping("/convert")
    public String convert(
            @RequestParam("startMonth") int startMonth,
            @RequestParam("endMonth") int endMonth,
            @RequestParam("orientation") int orientation
    ) {
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

    @PostMapping("/print-change-range")
    public void savePrintRange(
            @RequestParam("startMonth") int start,
            @RequestParam("endMonth") int end
    ) {
        startMonth = start;
        endMonth = end;
    }
}
