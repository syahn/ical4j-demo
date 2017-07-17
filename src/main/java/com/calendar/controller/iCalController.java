package com.calendar.controller;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by NAVER on 2017-07-14.
 */
@Controller
@EnableAutoConfiguration
public class iCalController {

    @GetMapping("/")
    public String index() throws IOException, ParserException {
        return "index";
    }
}


//
//    @PostMapping(value="/add")
//    public String add(@ModelAttribute CalendarData data, Model model){
//
//        //기존 데이터파일 불러오기(사용자의 고유 저장공간)
//        File file = new File("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");
//        ICalendar ical = null;
//        try {
//            ical = Biweekly.parse(file).first();//VCALENDAR component가 1개 존재한다고 가정
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //새로운 이벤트 생성
//        VEvent event = new VEvent();
//
//        //setting event infromation
//        event.setSummary(data.getEventSummary());//event summary set
//        event.setDateStart(data.getStartDate());//event startDate set
//        event.setDateEnd(data.getEndDate());//event endDate set
//        ical.addEvent(event);
//
//        try {
//            Biweekly.write(ical).go(file);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        model.addAttribute("resultData", data);
//
//        return "index";
//    }