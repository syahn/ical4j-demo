package com.calendar.controller;

import com.calendar.data.ICalEvent;
import com.calendar.data.ICalFilteredEvent;
import com.calendar.service.ICalService;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.filter.Filter;
import net.fortuna.ical4j.filter.PeriodRule;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.RRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public List<ICalFilteredEvent> resolveICalData(
        @RequestParam("month") int month,
        @RequestParam("year") int year
    ) throws IOException, ParserException, ParseException {

        iCal.setCurrenDate(year, month);

        //사용자 기존 캘린더 입력정보 ics로부터 불러오기
        Calendar calendar = iCal.parseFile(
                "/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/iCalData.ics");

        return iCal.resolveData(calendar);
    }
}

