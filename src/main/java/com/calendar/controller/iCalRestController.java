package com.calendar.controller;

import com.calendar.data.CalendarData;
import com.calendar.DefaultScheduleModel;
import com.calendar.data.ICalEvent;
import com.calendar.util.ProcessIcal;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.parameter.Role;
import net.fortuna.ical4j.model.property.*;
import net.fortuna.ical4j.util.UidGenerator;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by NAVER on 2017-07-17.
 */
@RestController
@EnableAutoConfiguration
public class iCalRestController {

    @GetMapping(value="/request-ical-data")
    public List<ICalEvent> processIcalData(@RequestParam("month") int month) throws IOException, ParserException, ParseException {

        //사용자 기존 캘린더 입력정보 ics로부터 불러오기
        Calendar calendar = ProcessIcal.parseIcalFile("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/advanced.ics");

        //각 이벤트의 정보(내용,날짜)를 ICalEvent오브젝트에 담기
        List<ICalEvent> dataList = ProcessIcal.resolveIcalDataToMemory(calendar);

        return ProcessIcal.filterByMonth(dataList, month);
    }

    @PostMapping("/create-new-calendar-file")
    public String createNewCalendarFile() throws IOException {

        Calendar calendar = new Calendar();
        calendar.getProperties().add(new ProdId("-//Ben Fortuna//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);

        return calendar.toString();
    }

    @PostMapping("/parse-calendar-string")
    public String parseCalendarString(
        @RequestParam("ical_string") String icalString
    ) throws IOException, ParserException {

        // string으로 저장
        String myCalendarString = icalString;

        // 캘린더 스트링 파싱
        StringReader sin = new StringReader(myCalendarString);
        CalendarBuilder newBuilder = new CalendarBuilder();
        Calendar parsedCalendar = newBuilder.build(sin);

        return parsedCalendar.toString();
    }

    @PostMapping("/parse-calendar-file")
    public String parseCalendarFile() throws IOException, ParserException {

        // 기존 캘린더 파일을 읽어 calendar 인스턴스 생성
        FileInputStream fin = new FileInputStream("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/meeting.ics");
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

        // Create a TimeZone
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

    @PostMapping("/create-new-event")
    public CalendarData createNewEvent(
            @RequestParam("event") String event,
            @RequestParam("date") String date
    ) throws IOException, ParserException, ParseException {

        // String을 Date형으로 전환
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsedDate = df.parse(date);

        // Calendar 인스턴스 생성 및 날짜 초기화
        FileInputStream fin = new FileInputStream("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");
        CalendarBuilder builder = new CalendarBuilder();
        Calendar originalCalendar = builder.build(fin);
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(parsedDate);

        // initialise as an all-day event..
        VEvent newEvent = new VEvent(new Date(calendar.getTime()), event);

        // Generate a UID for the event..
        UidGenerator ug = new UidGenerator("1");
        newEvent.getProperties().add(ug.generateUid());

        // 캘린더에 새로운 이벤트 추가
        originalCalendar.getComponents().add(newEvent);

        // 업데이트된 파일 저장
        FileOutputStream fout = new FileOutputStream("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");
        CalendarOutputter outputter = new CalendarOutputter();
        outputter.output(originalCalendar, fout);

        return new CalendarData(event, date, originalCalendar.toString());
    }

    @PostMapping("/parse-meeting-event")
    public DefaultScheduleModel parseMeetingEvent(
            @RequestParam("ical_string") String icalString
    ) throws IOException, ParserException, ParseException {

        SimpleDateFormat startTimePattern = new SimpleDateFormat("yyyy년 MM월 dd일 (E) a HH:mm");
        SimpleDateFormat endTimePattern = new SimpleDateFormat("a HH:mm '서울(GMT+09:00)'");

        // 기존 캘린더 파일을 읽어 calendar 인스턴스 생성
        CalendarBuilder builder = new CalendarBuilder();
        net.fortuna.ical4j.model.Calendar calendar = builder.build(new StringReader(icalString));

        // VEVENT 컴포넌트와 하위 데이터 추출
        Component component = calendar.getComponent("VEVENT");
        DateTime start = new DateTime(component.getProperty("DTSTART").getValue());
        DateTime end = new DateTime(component.getProperty("DTEND").getValue());
        String summary = component.getProperty("SUMMARY").getValue();
        String organizer = component.getProperty(Property.ORGANIZER).getValue();
        String location = component.getProperty(Property.LOCATION).getValue();
        List<String> attendeeList = new ArrayList<String>();

        // TODO: attendee의 이름과 메일만 추출하기
        for (Iterator i = component.getProperties(Property.ATTENDEE).iterator(); i.hasNext(); ) {
            Attendee attendee = (Attendee) i.next();
            String name = attendee.getValue();
            attendeeList.add(name);
        }

        String start_str = startTimePattern.format(start).toString();
        String end_str = endTimePattern.format(end).toString();

        return new DefaultScheduleModel(start_str, end_str, summary, location, organizer, attendeeList);

    }




}



//        //오늘만 종일 반복 일정인 이벤트만 필터링
//        java.util.Calendar today = java.util.Calendar.getInstance();
//        today.set(java.util.Calendar.HOUR_OF_DAY, 0);
//        today.clear(java.util.Calendar.MINUTE);
//        today.clear(java.util.Calendar.SECOND);
//        // create a period starting now with a duration of one (1) day..
//        Period period = new Period(new DateTime(today.getTime()), new Dur(1, 0, 0, 0));
//        Filter filter = new Filter(new PeriodRule(period));
//        List eventsToday = (List) filter.filter(calendar.getComponents(Component.VEVENT));
//
//
//
//        //일반복만 추려내기?
//        List<VEvent> events = calendar.getComponents(Component.VEVENT);