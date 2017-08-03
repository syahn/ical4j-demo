
/**
 * Created by NAVER on 2017-07-20.
 */

(function () {
    $(document).ready(function () {
        $.get("http://localhost:9000/load-iCalData",
            generateMonthObject()
        ).done(function (eventList) {
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
            appendPeriodEvent(event);
        } else {
            appendOneDayEvent(event);
        }
    }

    function appendPeriodEvent(event) {
        var color = selectColorByType(event.type);
        var weekRow = event.weekRow;
        var eventIdx = event.index;
        var period = event.period;
        var firstIdxOfWeek = weekRow * 7;
        var lastIdxOfWeek = weekRow * 7 + 7;
        
    }

    function appendOneDayEvent(event) {
        var color = selectColorByType(event.type);
        var weekRow = event.weekRow;
        var eventIdx = event.index;
        var firstIdxOfWeek = weekRow * 7;
        var lastIdxOfWeek = weekRow * 7 + 7;
        var isSlotExist = false;
        var tempLocation = 0;
        var lastLine = 2;

        for (var rowIdx = 2; rowIdx < 6; rowIdx++) {
            //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
            if (selectTr(weekRow, rowIdx).length != 0) {

                var slot = selectTd(weekRow, rowIdx, eventIdx);
                if (slot.html() == "&nbsp;") {
                    //빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                    tempLocation = tempLocation == 0 ? rowIdx : tempLocation;
                    isSlotExist = true;
                }
                lastLine++;
            }
        }

        if (isSlotExist) { // 빈공간 존재시
            selectTd(weekRow, tempLocation, eventIdx)
                .empty();
            selectTd(weekRow, tempLocation, eventIdx)
                .append(ondDayEvent(event, color));
            return;
        }

        //tr 존재 안하면 생성
        if (selectTr(weekRow, lastLine).length == 0) {
            addNewRow(weekRow);
            //dayIndex 부여한 tr로 갱신
            for (var idx = firstIdxOfWeek; idx < lastIdxOfWeek; idx++) {
                selectTr(weekRow, lastLine).append(blankEvent(idx));
            }
        }

        //&nbsp지우고 넣어야 css 깔끔
        selectTd(weekRow, lastLine, eventIdx)
            .empty();
        selectTd(weekRow, lastLine, eventIdx)
            .append(ondDayEvent(event, color));
        selectTd(weekRow, lastLine, eventIdx)
            .attr("colspan", 1);
    }

    function selectTr(weekRow, order) {
        return $(".table_container>div:nth-child(" + (weekRow + 1) + ")>" +
            ".schedule_list>tbody>tr:nth-child(" + order + ")");
    }

    function selectTd(weekRow, order, index) {
        return $(".table_container>div:nth-child(" + (weekRow + 1) + ")>" +
            ".schedule_list>tbody>tr:nth-child(" + order + ")>td[dayindex=" + index + "]");
    }

    function addNewRow(weekRow) {
        $(".table_container div:nth-child(" + (weekRow + 1) + ")>" +
            ".schedule_list>tbody")
            .append("<tr></tr>");
    }


    function periodEvent(event, color) {
        var index = event.index;
        var period = event.period;
        var summary = event.summary;

        return "<td dayindex='" + index + "' colspan = '" + period + "'>" +
            "<div style='background: " + color + ";'>" +
            "<span style='color: white;'>" + summary + "</span>" +
            "</div>" +
            "</td>";
    }

    function ondDayEvent(event, color) {
        var summary = event.summary;

        return "<div style='background: " + color + ";'>" +
            "<span style='color: white;'>" + summary + "</span>" +
            "</div>"
    }

    function blankEvent(index) {
        return "<td dayindex='" + index + "' colspan = '1'>&nbsp;</td>";
    }

    function compare(a, b) {
        if(a.isAnniversary > b.isAnniversary) return -1;
        else if (a.index < b.index) return -1;
        else if (a.index > b.index) return 1;
        else if (a.period > b.period) return -1;
        else if (a.period < b.period) return 1;
        else if (a.startHour < b.startHour) return -1;
        else if (a.startHour > b.startHour) return 1;
        return 0;
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
})();
