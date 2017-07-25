package com.calendar.controller;

import com.calendar.data.ICalFilteredEvent;
import com.calendar.service.ICalService;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.text.ParseException;
import java.util.List;

/**
 * Created by NAVER on 2017-07-17.
 */
@RestController
@EnableAutoConfiguration
public class ICalRestController {

    private ICalService iCal;

    @Autowired
    public ICalRestController(ICalService iCal) {
        this.iCal = iCal;
    }

    @GetMapping("/load-iCalData")
    public List<ICalFilteredEvent>  resolveICalData(
            @RequestParam("month") int month,
            @RequestParam("year") int year
    ) throws IOException, ParserException, ParseException {

        iCal.setCurrenDate(year, month);

        //사용자 기존 캘린더 입력정보 ics로부터 불러오기
        Calendar calendar = iCal.parseFile(
                "/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");

        List<ICalFilteredEvent> finalList = iCal.resolveData(calendar);

        //이벤트 데이터 적용된 새로운 html파일 생성해 저장하기
        //로컬 템플릿 불러오기 + 새로 쓰일 파일 생성해놓기
        File input = new File("/Users/NAVER/Desktop/ical4j-demo/src/main/resources/templates/month_" + Integer.toString(month) + ".html");
        Document doc = Jsoup.parse(input, "UTF-8");

        //불러온 기본 템플릿에 데이터 뿌리기
        renderingAllEvents(finalList, doc);

        String output = "/Users/NAVER/Desktop/ical4j-demo/target/classes/static/html/test.html";
        BufferedWriter htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        htmlWriter.write(doc.toString());
        htmlWriter.flush();
        htmlWriter.close();

        return finalList;
    }

    //script code
    public void renderingAllEvents(List<ICalFilteredEvent> eventList, Document doc) {
        for (int i = 0; i < eventList.size(); i++) {
            addEventToDom(eventList.get(i), doc);
        }
    }

    public void addEventToDom(ICalFilteredEvent event, Document doc) {
        String color = "blue";
        Elements elem = doc.select(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.getIndex() + "]");
        //output document에 append하기
        elem.append("<div style='background: " + color + ";'><span style='color: white;'>" + event.getSummary() + "</span></div>");
    }
}

