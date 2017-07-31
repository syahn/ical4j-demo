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
    public JsoupService(ICalService iCal) {
        this.iCal = iCal;
    }

    public void makeHTMLfiles(
            int startMonth,
            int endMonth
    ) throws IOException, ParserException, ParseException {

        Calendar calendar = parseIcalFile();

        for (int month = startMonth; month <= endMonth; month++) {

            List<ICalFilteredEvent> filteredEvents = iCal.filterData(calendar, month);

            File input = readTemplateByMonth(month);
            Document doc = parseHtml(input);
            drawEventsOnHtml(doc, filteredEvents);
            exportHtml(doc, month);
        }
    }

    public Calendar parseIcalFile() throws IOException, ParserException {
        return iCal.parseFile("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/period.ics");
    }

    private File readTemplateByMonth(int month) {
        return new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/templates/month_" + month + ".html");
    }

    private Document parseHtml(File input) throws IOException {
        return Jsoup.parse(
                input,
                "UTF-8",
                "http://localhost:9000/"
        );
    }

    private void drawEventsOnHtml(Document doc, List<ICalFilteredEvent> filteredEvents) {
        for (ICalFilteredEvent event : filteredEvents) {

            Elements slot = doc.select(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.getIndex() + "]");
            slot.append("<div ><span>" + event.getSummary() + "</span></div>");
        }

        //스크립트 태그 제거 - 마크업 중복 방지
        doc.select("script").remove();
    }

    private void exportHtml(Document doc, int month) throws IOException {
        //로컬에 새로운 html 파일로 저장
        String output = "/Users/NAVER/Desktop/ical4j-demo/target/classes/static/html/month" + Integer.toString(month) + ".html";
        BufferedWriter htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        htmlWriter.write(doc.toString());
        htmlWriter.flush();
        htmlWriter.close();
    }
}
