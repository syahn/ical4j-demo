package com.calendar.controller;

import com.calendar.service.JsoupService;
import com.calendar.service.PrintConverterService;
import net.fortuna.ical4j.data.ParserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    private final InMemoryUserDetailsManager inMemoryUserDetailsManager;

    @Autowired
    public PrintController(
            JsoupService jSoup,
            PrintConverterService converter,
            InMemoryUserDetailsManager inMemoryUserDetailsManager
    ) {
        this.jSoup = jSoup;
        this.converter = converter;
        this.inMemoryUserDetailsManager = inMemoryUserDetailsManager;
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
    public String findMyPath(Model model, @PathVariable String id){

        System.out.println(id);
        model.addAttribute("path", "/tempPDf/"+id+"/month_result.pdf");
        return "/pdf";

    }

    @RequestMapping("/tempPdf/{id}/"+"month_result.pdf")
    public String makeError(Model model, @PathVariable String id) {
        model.addAttribute("path", "/tempPDf /"+id+"/month_result.pdf");
        return "/pdf";

    }

    @PostMapping("/preview")
    public String viewPreviewWindow(
            Model model,
            @RequestParam("month") int month,
            @RequestParam("year") int year
    ) throws ParseException, ParserException, IOException {

        String fileID = UUID.randomUUID().toString();

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String currentPrincipalName = authentication.getName();
//        User user = (User) inMemoryUserDetailsManager.loadUserByUsername(currentPrincipalName);
//        user.setId(fileID);
//        inMemoryUserDetailsManager.updateUser(user);
//        System.out.println(((User) inMemoryUserDetailsManager.loadUserByUsername(currentPrincipalName)).getId());
        //user에 id 삽입

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
