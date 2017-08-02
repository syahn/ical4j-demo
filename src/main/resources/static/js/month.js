/**
 * Created by NAVER on 2017-07-20.
 */

(function () {
    $(document).ready(function () {
        $.get("http://localhost:9000/load-iCalData",
            generateMonthObject()
        ).done(function (eventList) {
            console.log(eventList);
            renderingAllEvents(eventList);
        });
    });

    function generateMonthObject() {
        var currentYear = parseInt($("._title").text().toString().substring(0, 5));
        var currentMonth = parseInt($(".print").attr("value"));
        return {
            "month": currentMonth,
            "year": currentYear
        };
    }

    function renderingAllEvents(eventList) {
        eventList.sort(compare);
        for (var i = 0; i < eventList.length; i++) {
            addEventToDom(eventList[i]);
        }
    }

    function addEventToDom(event) {
        if (event.type === "PERIOD") {
            appendEvent("period", event);
        }
        else {
            appendEvent("oneday", event);
        }
    }

    function selectColorByType(type) {
        switch (type) {
            case "DAY":
                return "blue";
            case "YEARLY":
                return "black";
            case "MONTHLY":
                return "green";
            case "WEEKLY":
                return "red";
            case "PERIOD":
                return "grey";
            case "DAILY":
                return "MediumPurple";
        }
    }

    function appendEvent(type, event) {
        var color = selectColorByType(event.type);
        var weekRow = event.weekRow;

        if (type === "period") {

            //기간일정 들어갈 tr존재하지 않는다면
            if ($(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(2)").length == 0) {
                appendEvent("row", event);//빈 tr생성
                //해당 인덱스 자리에 삽입 후 period만큼 기간 확장
                for (j = weekRow * 7; j < weekRow * 7 + 7; j++) {
                    if (event.index == j) {
                        $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(2)")
                            .append("<td dayindex='" + event.index + "' colspan = '" +
                                event.period + "'>" +
                                "<div style='background: " + color + ";'>" +
                                "<span style='color: white;'>" +
                                event.summary +
                                "</span></div></td>"
                            );
                        j += (event.period - 1);
                    } else {
                        //빈공간 &nbsp부여
                        $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(2)").append("<td dayindex='" + j + "' colspan = '1'>&nbsp;</td>");
                    }
                }
            }
            //기간일정 tr이 존재한다면 더 하위 tr생성 or 빈공간 들어갈 수 있으면 채우기
            else {
                //이미 생성된 상위 우선순위의 기간 일정의 tr라인 중 들어갈 자리 있는지
                var tempLocation = 0;
                var exist = false;
                var lastLine=2;

                for (i = 2; i < 6; i++) {
                    //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
                    if ($(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + i + ")").length != 0) {

                        var slot = $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + i + ")>td[dayindex=" + event.index + "]");
                        if (slot.html() == "&nbsp;") {
                            tempLocation = tempLocation == 0 ? i : tempLocation;//빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                            exist = true;
                        } else {// 빈자리 없는  tr이 라인에 존재한다면 다시 초기화
                            tempLocation = 0;
                            exist = false;
                        }
                        lastLine++;
                    }
                }

                if (exist) {//slot존재시

                    //뒤에 nbsp있는 td모두 제거
                    for(k=event.index+1;k<event.index+event.period;k++){
                        console.log("fdsafd");
                        console.log(k);
                        $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + tempLocation + ")>td[dayindex=" + k + "]").remove();
                    }

                    //해당 인덱스는 nbsp만 지우고 td남겨놓아야 추가가능
                    $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + tempLocation + ")>td[dayindex=" + event.index + "]").empty();
                    $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + tempLocation + ")>td[dayindex=" + event.index + "]").append("<div style='background: " + color + ";'>" +
                        "<span style='color: white;'>" +
                        event.summary +
                        "</span></div>");

                    console.log(event.period);

                    $(".schedule_list>tbody>tr:nth-child(" + tempLocation + ")>td[dayindex=" + event.index + "]")
                        .attr("colspan", event.period);

                    return;



                } else {
                    appendEvent("row", event);//빈 tr생성
                    //해당 인덱스 자리에 삽입 후 period만큼 기간 확장
                    for (j = weekRow * 7; j < weekRow * 7 + 7; j++) {
                        if (event.index == j) {
                            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + lastLine + ")")
                                .append("<td dayindex='" + event.index + "' colspan = '" +
                                    event.period + "'>" +
                                    "<div style='background: " + color + ";'>" +
                                    "<span style='color: white;'>" +
                                    event.summary +
                                    "</span></div></td>"
                                );
                            j += (event.period - 1);
                        } else {
                            //빈공간 &nbsp부여
                            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + lastLine + ")").append("<td dayindex='" + j + "' colspan = '1'>&nbsp;</td>");
                        }
                    }
                }
            }

        } else if (type === "oneday") {

            var exist = false;
            var tempLocation = 0;
            var lastLine = 2;

            for (i = 2; i < 6; i++) {
                //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
                if ($(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + i + ")").length != 0) {

                    var slot = $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + i + ")>td[dayindex=" + event.index + "]");
                    if (slot.html() == "&nbsp;") {
                        tempLocation = tempLocation == 0 ? i : tempLocation;//빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                        exist = true;
                    } else {// 빈자리 없는  tr이 라인에 존재한다면 다시 초기화
                        tempLocation = 0;
                        exist = false;
                    }
                    lastLine++;
                }
            }

            if (exist) { // 빈공간 존재시
                $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + tempLocation + ")>td[dayindex=" + event.index + "]").empty();
                $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + tempLocation + ")>td[dayindex=" + event.index + "]").append("<div style='background: " + color + ";'>" +
                    "<span style='color: white;'>" +
                    event.summary +
                    "</span></div>");
                return;
            }

            //tr4 존재 안하면 생성
            if ($(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + lastLine + ")").length == 0) {
                appendEvent("row", event);
                //dayIndex부여한 tr로 갱신
                for (j = weekRow * 7; j < weekRow * 7 + 7; j++) {
                    $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + lastLine + ")").append("<td dayindex='" + j + "' colspan = '1'>&nbsp;</td>");
                }
            }

            //&nbsp지우고 넣어야 css 깔끔
            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(" + lastLine + ")>td[dayindex=" + event.index + "]").empty();
            $(".schedule_list>tbody>tr:nth-child(" + lastLine + ")>td[dayindex=" + event.index + "]")
                .append("<div style='background: " + color + ";'>" +
                    "<span style='color: white;'>" +
                    event.summary +
                    "</span></div>");
            $(".schedule_list>tbody>tr:nth-child(" + lastLine + ")>td[dayindex=" + event.index + "]")
                .attr("colspan", 1);
        }
        //빈 tr라인 생성
        else if (type === "row") {
            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody")
                .append("<tr></tr>");
        }
    }

    function compare(a, b) {
        if (a.index < b.index) return -1;
        else if (a.index > b.index) return 1;
        else if (a.period > b.period) return -1;
        else if (a.period < b.period) return 1;
        else if (a.startHour < b.startHour) return -1;
        else if (a.startHour > b.startHour) return 1;
        return 0;
    }
})();

