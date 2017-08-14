package com.calendar.controller;

import com.calendar.service.JsoupService;
import com.calendar.service.PrintConverterService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.UUID;

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
            @RequestParam("fileID") String fileID,
            @RequestParam("currentYear") int currentYear
    ) throws ParseException, ParserException, IOException {

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear,fileID);

    }

    @RequestMapping("/tempPdf/{id}")
    public ResponseEntity findMyPath(Model model, @PathVariable String id) throws IOException {

        String filePath = "C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/tempPdf/" + id + "/month_result.pdf";
        InputStream inputStream = new FileInputStream(new File(filePath));
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
        HttpHeaders headers = new HttpHeaders();
        headers.add("content-disposition", "inline;filename=" + "dkdkd.pdf");
        headers.setContentLength(Files.size(Paths.get(filePath)));
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity(inputStreamResource, headers, HttpStatus.OK);

//        System.out.println(id);
//        model.addAttribute("path", "/tempPdf/"+id+"/month_result.pdf");
//        return "/pdf";

    }

    @PostMapping("/preview")
    public String viewPreviewWindow(
            Model model,
            @RequestParam("month") int month,
            @RequestParam("year") int year
    ) throws ParseException, ParserException, IOException {
        String fileID = UUID.randomUUID().toString();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();

        fileID = sessionId;

        System.out.println(sessionId);

        String currentPrincipalName = authentication.getName();
        System.out.println(currentPrincipalName);

        model.addAttribute("initialMonth",month);
        model.addAttribute("initialYear",year);
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
            @RequestParam("fileID") String fileID,
            @RequestParam("type") String type
    ) throws ParseException, ParserException, IOException, InterruptedException {
        //converting html to pdf - by url

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear,fileID);
        converter.makeAPdf(startMonth, endMonth, orientation, fileID, type);

        return "preview";
    }

}
