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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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


    @GetMapping("/tempPdf/{userID}/{fileID}-month_result.pdf")
    public ResponseEntity<byte[]> login2( @PathVariable String userID, @PathVariable String fileID) throws IOException, ParserException {
        InputStream inputStream = new FileInputStream("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static//tempPdf/"+userID+ "/" + fileID+"-month_result.pdf");
        byte[] contents = readFully(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = "output.pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(contents, headers, HttpStatus.OK);
        return response;
    }

    public static byte[] readFully(InputStream stream) throws IOException
    {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1)
        {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
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
            @RequestParam("fileID") String fileID,
            @RequestParam("type") String type
    ) throws ParseException, ParserException, IOException, InterruptedException {
        //converting html to pdf - by url

        jSoup.makeHTMLfiles(startMonth,endMonth,currentYear,fileID);
        converter.makeAPdf(startMonth, endMonth, orientation, fileID, type);

        return "preview";
    }

}
