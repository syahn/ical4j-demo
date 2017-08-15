package com.calendar.service;

/**
 * Created by NAVER on 2017-07-25.
 */

import com.calendar.data.ICalComparator;
import com.calendar.data.ICalFilteredData;
import com.calendar.data.ICalFilteredEvent;
import com.calendar.data.ICalTodo;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.ParseException;
import java.util.Collections;
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
            int endMonth,
            int currentYear,
            String userID,
            String fileID,
            int fontSize,
            String print_item
    ) throws IOException, ParserException, ParseException {

        Calendar calendar = parseIcalFile();

        for (int month = startMonth; month <= endMonth; month++) {

            ICalFilteredData filteredData = iCal.filterData(calendar, month, currentYear);
            File input = readTemplateByMonth(month, print_item);
            Document doc = parseHtml(input);

            drawEventsOnHtml(doc, filteredData, fontSize, print_item);

            exportHtml(doc, month, userID, fileID);
        }
    }

    public Calendar parseIcalFile() throws IOException, ParserException {
        return iCal.parseFile("/Users/Naver/Desktop/ical4j-demo/target/classes/static/iCalData/period.ics");
    }

    private File readTemplateByMonth(int month, String print_item) {

        if(print_item.equals("schedule")){
            return new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/templates/month_view/month" + month + "_Naver.html");
        }

        return new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/templates/month_view/month_" + month + ".html");
    }

    private Document parseHtml(File input) throws IOException {
        return Jsoup.parse(
                input,
                "UTF-8",
                "http://localhost:9000/"
        );
    }


    private void drawEventsOnHtml(Document doc, ICalFilteredData filteredData, int fontSize ,String print_item) {

        List<ICalTodo> todoList = filteredData.getTodoList();
        List<ICalFilteredEvent> eventList = filteredData.getEventList();

        Collections.sort(eventList,new ICalComparator());

        renderingAllEvents(doc, todoList, print_item);
        renderingAllEvents(doc, eventList, print_item);

        //스크립트 태그 제거 - 마크업 중복 방지
        doc.select("script").remove();
        doc.head().append("<link rel='stylesheet' href='../../css/font_by_size/" + Integer.toString(fontSize * 2 - 2) + ".css'>");
    }

    private void exportHtml(Document doc, int month, String userID, String fileID) throws IOException {
        File dirById = new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/html/" + userID);
        if (!dirById.exists()) {
            if (dirById.mkdir()) {
                System.out.println("Directory is created!");
            } else {
                System.out.println("Failed to create directory!");
            }
        }
        //로컬에 새로운 html 파일로 저장
        String output = "/Users/NAVER/Desktop/ical4j-demo/target/classes/static/html/"+
                userID +
                "/month" +
                Integer.toString(month) +
                "_" +
                fileID +
                ".html";

        BufferedWriter htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
        htmlWriter.write(doc.toString());
        htmlWriter.flush();
        htmlWriter.close();
    }

    private void renderingAllEvents(Document doc, List list, String print_item){

        for(int i=0;i<list.size();i++){

            if(list.get(i) instanceof ICalTodo){
                appendTodoEvent(doc, (ICalTodo) list.get(i));
            }
            else if(list.get(i) instanceof ICalFilteredEvent){
                if(((ICalFilteredEvent) list.get(i)).getType().equals("PERIOD")){
                    if(print_item.equals("schedule")){
                        continue;
                    }
                    appendPeriodEvent(doc, (ICalFilteredEvent) list.get(i));
                }else{
                    appenOneDayEvent(doc, (ICalFilteredEvent) list.get(i));
                }
            }
        }
    }

    private void appendTodoEvent(Document doc, ICalTodo todo) {
        String color = selectColorByType(todo.getType());
        int weekRow = todo.getWeekRow();
        int todoIdx = todo.getIndex();
        int firstIdxOfWeek = weekRow * 7;
        int lastIdxOfWeek = firstIdxOfWeek + 7;
        boolean isEmpty = false;
        int tempLocation = 0;
        int lastLine = 2;

        for (int rowIdx = 2; rowIdx < 6; rowIdx++) {
            //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
            if (!selectTr(doc, weekRow, rowIdx).isEmpty()) {
                Elements slot = selectTd(doc, weekRow, rowIdx, todoIdx);
                if (slot.html().equals("&nbsp;")) {
                    //빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                    tempLocation = tempLocation == 0 ? rowIdx : tempLocation;
                    isEmpty = true;
                }
                lastLine++;
            }
        }

        if (isEmpty) { // 빈공간 존재시
            selectTd(doc, weekRow, tempLocation, todoIdx)
                    .empty();
            selectTd(doc, weekRow, tempLocation, todoIdx)
                    .append(oneDayEvent(todo, color));
            return;
        }

        //tr4 존재 안하면 생성
        if (selectTr(doc, weekRow, lastLine).isEmpty()) {
            addNewRow(doc, weekRow);

            //dayIndex 부여한 tr로 갱신
            for (int idx = firstIdxOfWeek; idx < lastIdxOfWeek; idx++) {
                selectTr(doc, weekRow, lastLine).append(blankEvent(idx));
            }
        }

        //&nbsp지우고 넣어야 css 깔끔
        selectTd(doc, weekRow, lastLine, todoIdx)
                .empty();
        selectTd(doc, weekRow, lastLine, todoIdx)
                .append(oneDayEvent(todo, color));
    }

    private void appendPeriodEvent(Document doc, ICalFilteredEvent event) {
        String color = selectColorByType(event.getType());
        int weekRow = event.getWeekRow();
        int eventIdx = event.getIndex();
        int period = event.getPeriod();
        int firstIdxOfWeek = weekRow * 7;
        int lastIdxOfWeek = weekRow * 7 + 7;

        //기간일정 들어갈 tr존재하지 않는다면
        if (selectTr(doc, weekRow, 2).isEmpty()) {

            addNewRow(doc, weekRow);
            //해당 인덱스 자리에 삽입 후 period만큼 기간 확장
            for (int idx = firstIdxOfWeek; idx < lastIdxOfWeek; idx++) {
                if (idx == eventIdx) {
                    selectTr(doc, weekRow, 2).append(periodEvent(event, color));
                    idx += (period - 1);
                } else {
                    //빈공간 &nbsp부여
                    selectTr(doc, weekRow, 2).append(blankEvent(idx));
                }
            }
        }
        //기간일정 tr이 존재한다면 더 하위 tr생성 or 빈공간 들어갈 수 있으면 채우기
        else {
            //이미 생성된 상위 우선순위의 기간 일정의 tr라인 중 들어갈 자리 있는지
            int tempLocation = 0;
            boolean isEmpty = false;
            int lastLine = 2;

            for (int rowIdx = 2; rowIdx < 6; rowIdx++) {
                //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준

                if (!selectTr(doc, weekRow, rowIdx).isEmpty()) {

                    for (int tdIdx = eventIdx; tdIdx < eventIdx + period; tdIdx++) {
                        Elements slot = selectTd(doc, weekRow, rowIdx, tdIdx);
                        if (slot.html().equals("&nbsp;")) {
                            //빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                            tempLocation = tempLocation == 0 ? rowIdx : tempLocation;
                            isEmpty = true;
                        } else {// 빈자리 없는  tr이 라인에 존재한다면 다시 초기화
                            tempLocation = 0;
                            isEmpty = false;
                            break;
                        }
                    }
                    if (isEmpty) {
                        break;
                    }
                    lastLine++;
                }
            }

            if (isEmpty) {

                //뒤에 nbsp있는 td모두 제거
                for (int k = eventIdx + 1; k < eventIdx + period; k++) {
                    selectTd(doc, weekRow, tempLocation, k).remove();
                }

                //해당 인덱스는 nbsp만 지우고 td남겨놓아야 추가가능
                selectTd(doc, weekRow, tempLocation, eventIdx)
                        .empty();
                selectTd(doc, weekRow, tempLocation, eventIdx)
                        .append(oneDayEvent(event, color));
                selectTd(doc, weekRow, tempLocation, eventIdx)
                        .attr("colspan", Integer.toString(period));//////string 타입 변환 - 오류 확인하기

            } else {
                addNewRow(doc, weekRow);

                //해당 인덱스 자리에 삽입 후 peri`od만큼 기간 확장
                for (int idx = firstIdxOfWeek; idx < lastIdxOfWeek; idx++) {
                    if (idx == eventIdx) {
                        selectTr(doc, weekRow, lastLine).append(periodEvent(event, color));
                        idx += (period - 1);
                    } else {
                        //빈공간 &nbsp부여
                        selectTr(doc, weekRow, lastLine).append(blankEvent(idx));
                    }
                }
            }
        }
    }

    private void appenOneDayEvent(Document doc, ICalFilteredEvent event) {
        String color = selectColorByType(event.getType());
        int weekRow = event.getWeekRow();
        int eventIdx = event.getIndex();
        int firstIdxOfWeek = weekRow * 7;
        int lastIdxOfWeek = weekRow * 7 + 7;
        boolean isEmpty = false;
        int tempLocation = 0;
        int lastLine = 2;

        for (int rowIdx = 2; rowIdx < 6; rowIdx++) {
            //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
            if (!selectTr(doc, weekRow, rowIdx).isEmpty()) {////// 정확한 비교 연산자 확인하기

                Elements slot = selectTd(doc, weekRow, rowIdx, eventIdx);

                if (slot.html().equals("&nbsp;")) {
                    //빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                    tempLocation = tempLocation == 0 ? rowIdx : tempLocation;
                    isEmpty = true;
                } else if (event.getTimeLabel() != null) {//시간 일정이면 최하의 우선순위임으로 위에 채우지 못함
                    tempLocation = 0;
                    isEmpty = false;
                }
                lastLine++;
            }
        }

        if (isEmpty) { // 빈공간 존재시
            selectTd(doc, weekRow, tempLocation, eventIdx)
                    .empty();
            selectTd(doc, weekRow, tempLocation, eventIdx)
                    .append(oneDayEvent(event, color));
            return;
        }

        //tr 존재 안하면 생성
        if (selectTr(doc, weekRow, lastLine).isEmpty()) {
            addNewRow(doc, weekRow);
            //dayIndex 부여한 tr로 갱신
            for (int idx = firstIdxOfWeek; idx < lastIdxOfWeek; idx++) {
                selectTr(doc, weekRow, lastLine).append(blankEvent(idx));
            }
        }

        //&nbsp지우고 넣어야 css 깔끔
        selectTd(doc, weekRow, lastLine, eventIdx)
                .empty();
        selectTd(doc, weekRow, lastLine, eventIdx)
                .append(oneDayEvent(event, color));
    }

    private Elements selectTr(Document doc, int weekRow, int order) {
        return doc.select(".table_container>div:nth-child(" + (weekRow + 1) + ")>" +
                ".schedule_list>tbody>tr:nth-child(" + order + ")");
    }

    private Elements selectTd(Document doc, int weekRow, int order, int index) {
        return doc.select(".table_container>div:nth-child(" + (weekRow + 1) + ")>" +
                ".schedule_list>tbody>tr:nth-child(" + order + ")>td[dayindex=" + index + "]");
    }

    private void addNewRow(Document doc, int weekRow) {
        Elements slot = doc.select(".table_container div:nth-child(" + (weekRow + 1) + ")>" +
                ".schedule_list>tbody");
        slot.append("<tr></tr>");
    }

    private String periodEvent(ICalFilteredEvent event, String color) {
        int index = event.getIndex();
        int period = event.getPeriod();
        String summary = event.getSummary();

        return "<td dayindex='" + index + "' colspan = '" + period + "'>" +
                "<div style='background: " + color + ";'>" +
                "<span style='color: white;'>" + summary + "</span>" +
                "</div>" +
                "</td>";
    }

    private String oneDayEvent(Object event, String color) {
        String summary;
        if(event instanceof ICalTodo){
            summary = ((ICalTodo) event).getSummary();
        }else{
            summary = ((ICalFilteredEvent) event).getSummary();
        }
        return "<div colspan='1' style='background: " + color + ";'>" +
                "<span style='color: white;'>" + summary + "</span>" +
                "</div>";
    }

    private String blankEvent(int index) {
        return "<td dayindex='" + index + "' colspan = '1'>&nbsp;</td>";
    }

    private String selectColorByType(String type) {
        switch (type) {
            case "TODO":
                return "#f35055";
            case "DAY":
                return "blue";
            case "YEARLY":
                return "#ff9c71";
            case "MONTHLY":
                return "green";
            case "WEEKLY":
                return "red";
            case "PERIOD":
                return "#4984d9";
            case "DAILY":
                return "MediumPurple";
        }
        return "yellow";
    }
}
