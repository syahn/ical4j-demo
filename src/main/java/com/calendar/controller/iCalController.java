package com.calendar.controller;

import com.calendar.ICalEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.component.VEvent;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    @RequestMapping(value="/month_6")
    public String month_6(Model model) throws IOException, ParserException {

        //사용자 기존 캘린더 입력정보 ics로부터 불러오기
        FileInputStream fin = new FileInputStream("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");

        CalendarBuilder builder = new CalendarBuilder();

        Calendar calendar = builder.build(fin);

        List<ICalEvent> dataList = new ArrayList<>();

        //각 이벤트의 정보(내용,날짜)를 ICalEvent오브젝트에 담기  <- 여기서 해당 월에 필요한 데이터만 넣어주는게 나을 것!
        for(CalendarComponent event:calendar.getComponents(Component.VEVENT)){
            ICalEvent data = new ICalEvent();
            data.setSummary(event.getProperty(Property.SUMMARY).getValue());
            data.setStart(event.getProperty(Property.DTSTART).getValue());
            data.setEnd(event.getProperty(Property.DTEND).getValue());
            dataList.add(data);
        }

        //오늘만 종일 반복 일정인 이벤트만 필터링
        java.util.Calendar today = java.util.Calendar.getInstance();
        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
        today.clear(java.util.Calendar.MINUTE);
        today.clear(java.util.Calendar.SECOND);
        // create a period starting now with a duration of one (1) day..
        Period period = new Period(new DateTime(today.getTime()), new Dur(1, 0, 0, 0));
        Filter filter = new Filter(new PeriodRule(period));
        List eventsToday = (List) filter.filter(calendar.getComponents(Component.VEVENT));



        //일반복만 추려내기?
        List<VEvent> events = calendar.getComponents(Component.VEVENT);




        model.addAttribute("dataList",dataList);
        return "month_6";
    }

//    @RequestMapping(value = "/month_7")
//    public String month_7(Model model){
//
//        //사용자 기존 캘린더 입력정보 ics로부터 불러오기
//        File file = new File("C:/Users/NAVER/Desktop/iCalendar_demo/target/classes/static/iCalData/iCalData.ics");
//
//        //기존 입력정보의 이벤트들 리스트로 담기
//        ICalendar ical = null;
//        List<CalendarData> dataList = new ArrayList<>();
//        try {
//            ical = Biweekly.parse(file).first();//VCALENDAR는 유일하다 가정
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//
//            //각 이벤트의 정보(내용,날짜)를 Calendar오브젝트에 담기
//            for(VEvent event:ical.getEvents()){
//                CalendarData data = new CalendarData();
//                data.setEventSummary(event.getSummary().getValue());
//                data.setStartDate(event.getDateStart().getValue());
//                data.setEndDate(event.getDateEnd().getValue());
//                dataList.add(data);
//            }
//        }
//
//        model.addAttribute("dataList",dataList);
//        return "month_7";
//    }



}