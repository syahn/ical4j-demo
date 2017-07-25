package com.calendar.service;

/**
 * Created by NAVER on 2017-07-25.
 */

import com.calendar.data.ICalFilteredEvent;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.util.List;

@Service
public class JsoupService {

    private ICalService iCal;

    @Autowired
    public JsoupService( ICalService iCal) {
        this.iCal = iCal;
    }

    public void makeHTMLfiles(int startMonth, int endMonth) throws IOException, ParserException, ParseException {

        Calendar calendar = iCal.parseFile(
                "/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");
        for (int month = startMonth; month <= endMonth; month++) {
            System.out.println(Integer.toString(month) + Integer.toString(startMonth) + Integer.toString(endMonth));
            List<ICalFilteredEvent> filteredEvents = iCal.resolveData(calendar, month);

            File input = new File("C:/Users/NAVER/Desktop/ical4j-demo/src/main/resources/templates/month_" + month + ".html");
            Document doc = Jsoup.parse(input, "UTF-8", "http://localhost:9000/");;

            for (ICalFilteredEvent event : filteredEvents) {
                Elements slot = doc.select(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.getIndex() + "]");
                slot.append("<div ><span>" + event.getSummary() + "</span></div>");
            }

            //로컬에 새로운 html 파일로 저장
            String output = "/Users/NAVER/Desktop/ical4j-demo/src/main/resources/static/html/" + Integer.toString(month) +".html";
            BufferedWriter htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
            htmlWriter.write(doc.toString());
            htmlWriter.flush();
            htmlWriter.close();
        }
    }
}
