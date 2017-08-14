package com.calendar.controller;

import com.calendar.service.JsoupService;
import com.calendar.service.PrintConverterService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            @RequestParam("userID") String userID,
            @RequestParam("fileID") String fileID,
            @RequestParam("currentYear") int currentYear
    ) throws ParseException, ParserException, IOException {

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear,userID,fileID);
    }


    @GetMapping("/html/{userID}/month{startMonth}_{fileID}.html")
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

    @RequestMapping("/tempPdf/{userID}/{fileID}/print-request")
    public String findMyPath(Model model, @PathVariable String userID, @PathVariable String fileID){

        model.addAttribute("path", "/tempPdf/"+userID+"/"+fileID+".pdf");
        return "/pdf";

    }

//    @GetMapping("/tempPdf/{userID}/{fileID}-month_result.pdf")
//    public ResponseEntity<byte[]> login2(@PathVariable String userID, @PathVariable String fileID) throws IOException, ParserException {
//        Path pdfPath = Paths.get("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/tempPdf/"+userID+ "/" + fileID+"-month_result.pdf");
//        byte[] contents = Files.readAllBytes(pdfPath);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("application/pdf"));
//        String filename = "output.pdf";
//        headers.setContentDispositionFormData("inline", filename);
//        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
//        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
//        return response;
//    }

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
            @RequestParam("userID") String userID,
            @RequestParam("fileID") String fileID,
            @RequestParam("type") String type
    ) throws ParseException, ParserException, IOException, InterruptedException {
        //converting html to pdf - by url

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear, userID, fileID);
        converter.makeAPdf(startMonth, endMonth, orientation, userID, fileID, type);

        return "preview";
    }

}
