package com.calendar.controller;

import com.calendar.service.JsoupService;
import com.calendar.service.PrintConverterService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

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
    @PostAuthorize("returnObject.type == authentication.name")
    public void makePreview(
            @RequestParam("startMonth") int startMonth,
            @RequestParam("endMonth") int endMonth,
            @RequestParam("fontSize") int fontSize,
            @RequestParam("userID") String userID,
            @RequestParam("fileID") String fileID,
            @RequestParam("currentYear") int currentYear
    ) throws ParseException, ParserException, IOException {
        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear, fontSize, userID,fileID);
    }

    @GetMapping("/html/{userID}/{startMonth}/{fileID}/html-request")
    @ResponseBody
    public String responseHtml(
            @PathVariable String userID,
            @PathVariable String startMonth,
            @PathVariable String fileID
    ) throws IOException, ParserException {
        Path htmlPath = Paths.get("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/html/"+userID+ "/month" + startMonth+"_" + fileID + ".html");
        byte[] contents = Files.readAllBytes(htmlPath);

        return new String(contents);
    }

    @GetMapping("/tempPdf/{userID}/{fileID}/pdf-request")
    public String responseDir(
            @PathVariable String userID,
            @PathVariable String fileID,
            Model model
    ) throws IOException, ParserException {
        model.addAttribute("path", "/tempPdf/" + userID + "/" + fileID + ".pdf");

        return "/pdf";
    }

    @PostMapping("/preview")
    public String viewPreviewWindow(
            Model model,
            @RequestParam("month") int month,
            @RequestParam("year") int year
    ) throws ParseException, ParserException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userID = authentication.getName();
        String fileID = UUID.randomUUID().toString();

        model.addAttribute("initialMonth",month);
        model.addAttribute("initialYear",year);
        model.addAttribute("userID",userID);
        model.addAttribute("fileID",fileID);

        return "preview";
    }

    //converter for pdf save and print
    @PostMapping("/convert")
    public String convert(
            @RequestParam("startMonth") int startMonth,
            @RequestParam("endMonth") int endMonth,
            @RequestParam("currentYear") int currentYear,
            @RequestParam("orientation") int orientation,
            @RequestParam("fontSize") int fontSize,
            @RequestParam("userID") String userID,
            @RequestParam("fileID") String fileID,
            @RequestParam("type") String type
    ) throws ParseException, ParserException, IOException, InterruptedException {
        //converting html to pdf - by url

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear, fontSize, userID, fileID);
        converter.makeAPdf(startMonth, endMonth, orientation, userID, fileID, type);

        return "preview";
    }

}
