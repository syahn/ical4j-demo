package com.calendar.util;

import com.calendar.data.ICalEvent;
import com.calendar.data.ICalFilteredEvent;
import com.calendar.data.Setting;
import com.calendar.service.ICalService;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.component.VEvent;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * Created by NAVER on 2017-08-22.
 */
public class FilterUtilTest {
    @Test
    public void makeValidPeriod() throws Exception {
    }

    @Test
    public void calIndexOfLastWeekRecurEvent() throws Exception {
    }

    @Test
    public void calIndexOfDayRecurEvent() throws Exception {
    }

    @Test
    public void VTodoToICalTodo() throws Exception {
    }

    @Test
    public void VEventToICalEvent() throws Exception {
    }

    @Test
    public void calculateIndexOfDate() throws Exception {
    }

    @Test
    public void calculatePeriod() throws Exception {
    }

    @Test
    public void addEventToFilteredEvents() throws Exception {
    }

    @Test
    public void makeFilteredEvent() throws Exception {
    }

    @Test
    public void isVaildEvent() throws Exception {
    }

    @Test
    public void filterRecurEvent() throws Exception {
    }

    @Test
    public void test_월별마지막날_정확한인덱스값() throws IOException, ParserException {
        ICalService ical = new ICalService();

        Calendar calendar = ical.parseFile("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/EndDay.ics");
        VEvent tempVEvent_28 = (VEvent) calendar.getComponents("VEVENT").get(0);
        VEvent tempVEvent_29 = (VEvent) calendar.getComponents("VEVENT").get(1);
        VEvent tempVEvent_30 = (VEvent) calendar.getComponents("VEVENT").get(2);
        VEvent tempVEvent_31 = (VEvent) calendar.getComponents("VEVENT").get(3);
        VEvent tempVEvent_31_JAN = (VEvent) calendar.getComponents("VEVENT").get(4);

        Setting setting_28 = ical.setUp(2017,2);
        Setting setting_29 = ical.setUp(2016, 2);
        Setting setting_30 = ical.setUp(2017,6);
        Setting setting_31 = ical.setUp(2017,7);
        Setting setting_31_JAN = ical.setUp(2017,1);


        ICalEvent event_28 = FilterUtil.VEventToICalEvent(tempVEvent_28, setting_28);
        ICalEvent event_29 = FilterUtil.VEventToICalEvent(tempVEvent_29, setting_29);
        ICalEvent event_30 = FilterUtil.VEventToICalEvent(tempVEvent_30, setting_30);
        ICalEvent event_31 = FilterUtil.VEventToICalEvent(tempVEvent_31, setting_31);
        ICalEvent event_31_JAN = FilterUtil.VEventToICalEvent(tempVEvent_31_JAN, setting_31_JAN);

        List<ICalFilteredEvent> resultList = new ArrayList<>();

        //result list
        FilterUtil.filterMonthly(event_28,resultList,setting_28);
        FilterUtil.filterMonthly(event_29,resultList,setting_29);
        FilterUtil.filterMonthly(event_30,resultList,setting_30);
        FilterUtil.filterMonthly(event_31,resultList,setting_31);
        FilterUtil.filterMonthly(event_31_JAN,resultList,setting_31_JAN);

        int result_28 = resultList.get(0).getIndex();
        int result_29 = resultList.get(1).getIndex();
        int result_30 = resultList.get(2).getIndex();
        int result_31 = resultList.get(3).getIndex();
        int result_31_JAN = resultList.get(4).getIndex();

        //period>1되야함
        assertThat("28일 마지막날",result_28, is(30));
        assertThat("29일 마지막날",result_29, is(29));
        assertThat("30일 마지막날",result_30, is(33));
        assertThat("31일 마지막날",result_31, is(36));
        assertThat("1월 31일 마지막날",result_31, is(30));

    }

    @Test
    public void test_몇번째_요일_일정이_잘_렌더링_되는지() throws Exception {
        // given
        ICalService ical = new ICalService();
        Calendar calendar1 = ical.parseFile("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/test_1번째월요일_idx1.ics");
        Calendar calendar8 = ical.parseFile("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/test_1번째월요일_idx8.ics");

        VEvent tempEvent1 = (VEvent) calendar1.getComponents("VEVENT").get(0);
        VEvent tempEvent8 = (VEvent) calendar8.getComponents("VEVENT").get(0);

        Setting setting1 = ical.setUp(2017,10);
        Setting setting8 = ical.setUp(2017,8);

        ICalEvent event1 = FilterUtil.VEventToICalEvent(tempEvent1, setting1);
        ICalEvent event8 = FilterUtil.VEventToICalEvent(tempEvent8, setting8);

        List<ICalFilteredEvent> filteredEventList1 = new ArrayList<>();
        List<ICalFilteredEvent> filteredEventList8 = new ArrayList<>();

        // when
        FilterUtil.filterMonthly(event1, filteredEventList1, setting1);
        FilterUtil.filterMonthly(event8, filteredEventList8, setting8);

//        System.out.println(filteredEventList1.get(0).getEndIndex())
        // then
        assertThat("인덱스가 1이어야 한다.", filteredEventList1.get(0).getIndex(), is(1));
        assertThat("인덱스가 8이어야 한다.", filteredEventList8.get(0).getIndex(), is(8));

    }


    @Test
    public void test_시간일정_하루이상일때_period값() throws IOException, ParserException {
        ICalService ical = new ICalService();
        Calendar calendar = ical.parseFile("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/TimeEvent.ics");
        VEvent tempVEvent = (VEvent) calendar.getComponents("VEVENT").get(0);
        Setting setting = ical.setUp(2017,8);
        ICalEvent event = FilterUtil.VEventToICalEvent(tempVEvent, setting);

        //result
        int result = FilterUtil.calculatePeriod(event, tempVEvent);


        //period>1되야함
        assertThat("시간일정이 하루 이상 일떄",result, is(not(1)));

    }

    @Test
    public void test_기간7이하인_기간일정_2주_걸칠때() throws Exception {

        ICalService ical = new ICalService();
        Calendar calendar = ical.parseFile("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/ShortPeriod2week.ics");
        VEvent tempEvent = (VEvent) calendar.getComponents("VEVENT").get(0);
        Setting setting = ical.setUp(2017,8);
        ICalEvent event = FilterUtil.VEventToICalEvent(tempEvent, setting);

        //result list
        List<ICalFilteredEvent> resultList = new ArrayList<>();
        FilterUtil.filterNoneRecurEvent(event,resultList);

        //split된 개수 맞는지
        assertEquals(2,resultList.size());//무조건 2 되야함

    }

    @Test
    public void test_이전달부터_다음달까지_걸친기간일정_Split개수() throws Exception {

        ICalService ical = new ICalService();
        Calendar calendar = ical.parseFile("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/iCalData/LongPeriod.ics");
        VEvent tempEvent = (VEvent) calendar.getComponents("VEVENT").get(0);
        Setting setting = ical.setUp(2017,8);
        ICalEvent event = FilterUtil.VEventToICalEvent(tempEvent, setting);

        //result list
        List<ICalFilteredEvent> resultList = new ArrayList<>();
        FilterUtil.filterNoneRecurEvent(event,resultList);

        //split된 개수 맞는지
        assertEquals(5,resultList.size());

    }

    @Test
    public void test_이전달부터_현재달까지_걸친기간일정_Split개수() throws Exception {

    }

    @Test
    public void test_현재달부터_다음달까지_걸친기간일정_Split개수() throws Exception {

    }

    @Test
    public void test_현재달에만있는_기간일정_Split개수() throws Exception {

    }


    @Test
    public void filterDaily() throws Exception {

//
//        //daily 일정만 있는 ics 파싱해서 ICalevent로 만들기
//        ICalEvent event = new ICalEvent("uid","dateString");
//
//        //list
//        List<ICalFilteredEvent> filteredEventList = new ArrayList<>();
//
//        //
//        FilterUtil.FilterDaily(event,filteredEventList);
//
//        //확인
//        assert(filteredEventList, expected);


    }

    @Test
    public void filterWeekly() throws Exception {
    }

    @Test
    public void filterMonthly() throws Exception {
    }

    @Test
    public void filterYearly() throws Exception {
    }

    @Test
    public void addLastWeekRecurEventToFilteredEvents() throws Exception {
    }

    @Test
    public void addDayRecurEventToFilteredEvents() throws Exception {
    }

}