package com.calendar.controller;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.GregorianCalendar;

/**
 * Created by NAVER on 2017-07-17.
 */
@RestController
@EnableAutoConfiguration
public class iCalRestController {

    @PostMapping("/create-new-calendar-file")
    public String createNewCalendarFile() {

        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        return calendar.toString();
    }

    @PostMapping("/parse-calendar-string")
    public String parseCalendarString() throws IOException, ParserException {

        // 기존 캘린더 파일을 읽어 calendar 인스턴스 생성
        FileInputStream fin = new FileInputStream("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(fin);

        // string으로 저장
        String myCalendarString = calendar.toString();

        // 캘린더 스트링 파싱
        StringReader sin = new StringReader(myCalendarString);
        CalendarBuilder newBuilder = new CalendarBuilder();
        Calendar parsedCalendar = newBuilder.build(sin);

        return parsedCalendar.toString();
    }

    @PostMapping("/parse-calendar-file")
    public String parseCalendarFile() throws IOException, ParserException {

        // 기존 캘린더 파일을 읽어 calendar 인스턴스 생성
        FileInputStream fin = new FileInputStream("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");
        CalendarBuilder builder = new CalendarBuilder();
        Calendar calendar = builder.build(fin);

        return calendar.toString();
    }

    @PostMapping("/create-allday-event")
    public String createAllDayEvent() throws IOException, ParserException {


        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.set(java.util.Calendar.MONTH, java.util.Calendar.DECEMBER);
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 25);

        // initialise as an all-day event..
        VEvent christmas = new VEvent(new Date(calendar.getTime()), "Christmas Day");

        // Generate a UID for the event..
        UidGenerator ug = new UidGenerator("1");
        christmas.getProperties().add(ug.generateUid());

        net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
        cal.getComponents().add(christmas);

        return cal.toString();
    }

    @PostMapping("/create-fourhour-event")
    public String createFourHourEvent() throws IOException, ParserException {

        //     Create a TimeZone
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        TimeZone timezone = registry.getTimeZone("America/Mexico_City");
        VTimeZone tz = timezone.getVTimeZone();

        // Start Date is on: April 1, 2008, 9:00 am
        java.util.Calendar startDate = new GregorianCalendar();
        startDate.setTimeZone(timezone);
        startDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
        startDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
        startDate.set(java.util.Calendar.YEAR, 2008);
        startDate.set(java.util.Calendar.HOUR_OF_DAY, 9);
        startDate.set(java.util.Calendar.MINUTE, 0);
        startDate.set(java.util.Calendar.SECOND, 0);

        // End Date is on: April 1, 2008, 13:00
        java.util.Calendar endDate = new GregorianCalendar();
        endDate.setTimeZone(timezone);
        endDate.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
        endDate.set(java.util.Calendar.DAY_OF_MONTH, 1);
        endDate.set(java.util.Calendar.YEAR, 2008);
        endDate.set(java.util.Calendar.HOUR_OF_DAY, 13);
        endDate.set(java.util.Calendar.MINUTE, 0);
        endDate.set(java.util.Calendar.SECOND, 0);

        // Create the event
        String eventName = "Progress Meeting";
        DateTime start = new DateTime(startDate.getTime());
        DateTime end = new DateTime(endDate.getTime());
        VEvent meeting = new VEvent(start, end, eventName);

        // add timezone info..
        meeting.getProperties().add(tz.getTimeZoneId());

        // generate unique identifier..
        UidGenerator ug = new UidGenerator("uidGen");
        Uid uid = ug.generateUid();
        meeting.getProperties().add(uid);

        // add attendees..
        Attendee dev1 = new Attendee(URI.create("mailto:dev1@mycompany.com"));
        dev1.getParameters().add(Role.REQ_PARTICIPANT);
        dev1.getParameters().add(new Cn("Developer 1"));
        meeting.getProperties().add(dev1);

        Attendee dev2 = new Attendee(URI.create("mailto:dev2@mycompany.com"));
        dev2.getParameters().add(Role.OPT_PARTICIPANT);
        dev2.getParameters().add(new Cn("Developer 2"));
        meeting.getProperties().add(dev2);

        // Create a calendar
        net.fortuna.ical4j.model.Calendar icsCalendar = new net.fortuna.ical4j.model.Calendar();
        icsCalendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        icsCalendar.getProperties().add(Version.VERSION_2_0);
        icsCalendar.getProperties().add(CalScale.GREGORIAN);

        // Add the event and print
        icsCalendar.getComponents().add(meeting);


        return icsCalendar.toString();
    }
}
//
//    // 이벤트 날짜 생성하기
//
//
//
//                // 캘린더 파일 생성하기
//                FileOutputStream fout = new FileOutputStream("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData2.ics");
//
//                CalendarOutputter outputter = new CalendarOutputter();
//                outputter.output(icsCalendar, fout);
////
////
//        // Reading the file and creating the calendar
//        CalendarBuilder builder = new CalendarBuilder();
//        Calendar cal = null;
//        try {
//            cal = builder.build(new FileInputStream("my.ics"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParserException e) {
//            e.printStackTrace();
//        }
//
//
//// Create the date range which is desired.
//        DateTime from = new DateTime("20100101T070000Z");
//        DateTime to = new DateTime("20100201T070000Z");;
//        Period period = new Period(from, to);
//
//
//// For each VEVENT in the ICS
//        for (Object o : cal.getComponents("VEVENT")) {
//            Component c = (Component)o;
//            PeriodList list = c.calculateRecurrenceSet(period);
//
//            for (Object po : list) {
//                System.out.println((Period)po);
//            }
//        }


//
//        ICalendar ical = null;
//        List<CalendarDataString> dataList = new ArrayList<>();
//
//        try {
//            ical = Biweekly.parse(file).first();//VCALENDAR는 유일하다 가정
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//
//            //각 이벤트의 정보(내용,날짜)를 Calendar오브젝트에 담기
//
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//            for(VEvent event:ical.getEvents()){
//                CalendarDataString data = new CalendarDataString();
//                data.setEventSummary(event.getSummary().getValue());
//                data.setStartDate(formatter.format(event.getDateStart().getValue()));
//                data.setEndDate(formatter.format(event.getDateEnd().getValue()));
//                dataList.add(data);
//            }
//        }
//