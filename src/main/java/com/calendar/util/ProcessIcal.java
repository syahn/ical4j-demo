package com.calendar.util;

import com.calendar.data.ICalEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.CalendarComponent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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
            data.setDate(extractDate(data.getStart()));

            dataList.add(data);
        }

        return dataList;
    }

    public static List<ICalEvent> filterByMonth(List<ICalEvent> iCalList, int month) {
        List<ICalEvent> filteredList = new ArrayList<>();

        for (ICalEvent event : iCalList){
            // 해당 월뿐만 아니라 전월, 다음월도 포함
            if (month - 1 <= event.getMonth() && event.getMonth() <= month + 1) {
                filteredList.add(event);
            }
        }

        return filteredList;
    }

    private static int extractMonth(String time) {
        return Integer.parseInt(time.substring(4,6));
    }
    private static int extractDate(String time) {
        return Integer.parseInt(time.substring(6,8));
    }

}
