package com.calendar.util;

import com.calendar.data.ICalEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.CalendarComponent;
import net.fortuna.ical4j.model.property.RRule;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static net.fortuna.ical4j.model.Property.RRULE;

/**
 * Created by NAVER on 2017-07-20.
 */
public class ProcessIcal {

    public static Calendar parseIcalFile(String file) throws IOException, ParserException {
        FileInputStream fin = new FileInputStream(file);
        CalendarBuilder builder = new CalendarBuilder();

        return builder.build(fin);
    }

    public static List<ICalEvent> resolveIcalDataToMemory(Calendar calendar) throws ParseException {
        List<ICalEvent> dataList = new ArrayList<>();

        for (Component event : calendar.getComponents(Component.VEVENT)) {
            ICalEvent data = new ICalEvent();
            data.setSummary(event.getProperty(Property.SUMMARY).getValue());
            data.setStart(event.getProperty(Property.DTSTART).getValue());
            data.setEnd(event.getProperty(Property.DTEND).getValue());
            data.setMonth(extractMonth(data.getStart()));
            data.setStartDate(extractDate(data.getStart()));

            Property recurrency = event.getProperty("RRULE");
            if (recurrency != null) {
                Recur recurrProp = ((RRule)recurrency).getRecur();
                data.setRecur(true);
                data.setFrequency(recurrProp.getFrequency());
                data.setInterval(recurrProp.getInterval());
                data.setUntil(recurrProp.getUntil().toString());
                data.setUntilYear(extractYear(data.getUntil()));
                data.setUntilMonth(extractMonth(data.getUntil()));
                data.setUntilDate(extractDate(data.getUntil()));

            } else {
                data.setRecur(false);
            }
            dataList.add(data);
        }

        return dataList;
    }

    public static List<ICalEvent> filterByMonth(List<ICalEvent> iCalList, int month) {
        List<ICalEvent> filteredList = new ArrayList<>();

        for (ICalEvent event : iCalList) {
            // 해당 월뿐만 아니라 전월, 다음월도 포함
            if (month == event.getMonth()) {
                filteredList.add(event);
            } else if (month == event.getMonth() + 1 && event.getStartDate() >= 23) {
                filteredList.add(event);
            } else if (month == event.getMonth() - 1 && event.getStartDate() <= 6) {
                filteredList.add(event);
            } else if (event.getRecur()) {
                filteredList.add(event);
            }
        }

        return filteredList;
    }

//    TODO: 정확한 6일 값 구하기
//    private static int dateIncluded(ICalEvent event) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//        Date convertedDate = dateFormat.parse(date);
//
//        Calendar c = Calendar.getInstance();
//        c.setTime(convertedDate);
//        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
//    }

    private static int extractYear(String time) {
        return Integer.parseInt(time.substring(0,4));
    }
    private static int extractMonth(String time) {return Integer.parseInt(time.substring(4,6));}
    private static int extractDate(String time) {
        return Integer.parseInt(time.substring(6,8));
    }

}
