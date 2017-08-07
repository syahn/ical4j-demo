/**
 * Created by NAVER on 2017-07-20.
 */

(function () {
    $(document).ready(function () {
        $.get("http://localhost:9000/load-iCalData",
            generateMonthObject()
        ).done(function (dataList) {
            renderingAllEvents(dataList);
        });
    });

    function generateMonthObject() {
        var currentYear = parseInt($("._title").text().toString().substring(0, 5));
        var currentMonth = parseInt($(".print").attr("value"));

        return {
            "year": currentYear,
            "month": currentMonth
        };
    }

    function renderingAllEvents(dataList) {
        var eventList = dataList.eventList;
        var todoList = dataList.todoList;

        for (var i = 0; i < todoList.length; i++) {
            addEventToDom(todoList[i]);
        }

        for (var i = 0; i < eventList.length; i++) {
            addEventToDom(eventList[i]);
        }
    }

    function addEventToDom(data) {
        if (data.type === "PERIOD") {
            appendPeriodEvent(data);
        }
        else {
            appendOneDayEvent(data);
        }
    }

    function appendPeriodEvent(event) {
        var color = selectColorByType(event.type);
        var weekRow = event.weekRow;
        var eventIdx = event.index;
        var period = event.period;
        var firstIdxOfWeek = weekRow * 7;
        var lastIdxOfWeek = firstIdxOfWeek + 7;

        //기간일정 들어갈 tr이 존재하지 않는다면
        if (selectTr(weekRow, 2).length === 0) {
            addNewRow(weekRow);
            //해당 인덱스 자리에 삽입 후 period만큼 기간 확장
            for (var idx = firstIdxOfWeek; idx < lastIdxOfWeek; idx++) {
                if (idx === eventIdx) {
                    selectTr(weekRow, 2).append(periodEvent(event, color));
                    idx += (period - 1);
                } else {
                    //빈공간 &nbsp부여
                    selectTr(weekRow, 2).append(blankEvent(idx));
                }
            }
        }
        //기간일정 tr이 존재한다면 더 하위 tr생성 or 빈공간 들어갈 수 있으면 채우기
        else {
            //이미 생성된 상위 우선순위의 기간 일정의 tr라인 중 들어갈 자리 있는지
            var tempLocation = 0;
            var isEmpty = false;
            var lastLine = 2;

            for (var rowIdx = 2; rowIdx < 6; rowIdx++) {
                //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
                if (selectTr(weekRow, rowIdx).length !== 0) {
                    for (var tdIdx = eventIdx; tdIdx < eventIdx + period; tdIdx++) {
                        var slot = selectTd(weekRow, rowIdx, tdIdx);
                        if (slot.html() == "&nbsp;") {
                            //빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                            tempLocation = tempLocation === 0 ? rowIdx : tempLocation;
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
                for (var k = eventIdx + 1; k < eventIdx + period; k++) {
                    selectTd(weekRow, tempLocation, k).remove();
                }

                //해당 인덱스는 nbsp만 지우고 td남겨놓아야 추가가능
                selectTd(weekRow, tempLocation, eventIdx)
                    .empty();
                selectTd(weekRow, tempLocation, eventIdx)
                    .append(ondDayEvent(event, color));
                selectTd(weekRow, tempLocation, eventIdx)
                    .attr("colspan", period);

                return;

            } else {
                addNewRow(weekRow);

                //해당 인덱스 자리에 삽입 후 peri`od만큼 기간 확장
                for (var idx = firstIdxOfWeek; idx < lastIdxOfWeek; idx++) {
                    if (idx === eventIdx) {
                        selectTr(weekRow, lastLine).append(periodEvent(event, color));
                        idx += (period - 1);
                    } else {
                        //빈공간 &nbsp부여
                        selectTr(weekRow, lastLine).append(blankEvent(idx));
                    }
                }
            }
        }
    }

    function appendOneDayEvent(event) {
        var color = selectColorByType(event.type);
        var weekRow = event.weekRow;
        var eventIdx = event.index;
        var firstIdxOfWeek = weekRow * 7;
        var lastIdxOfWeek = firstIdxOfWeek + 7;
        var isEmpty = false;
        var tempLocation = 0;
        var lastLine = 2;

        for (var rowIdx = 2; rowIdx < 6; rowIdx++) {
            //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
            if (selectTr(weekRow, rowIdx).length != 0) {
                var slot = selectTd(weekRow, rowIdx, eventIdx);

                if (slot.html() == "&nbsp;") {
                    //빈자리있는 tr라인을 이미 찾았을 경우 templocation유지
                    tempLocation = tempLocation == 0 ? rowIdx : tempLocation;
                    isEmpty = true;
                } else if (event.timeLabel !== null) {//시간 일정이면 최하의 우선순위임으로 위에 채우지 못함
                    tempLocation = 0;
                    isEmpty = false;
                }
                lastLine++;
            }
        }

        if (isEmpty) { // 빈공간 존재시
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
        return "<div colspan='1' style='background: " + color + ";'>" +
            "<span style='color: white;'>" + summary + "</span>" +
            "</div>";
    }

    function blankEvent(index) {
        return "<td dayindex='" + index + "' colspan = '1'>&nbsp;</td>";
    }

    function selectColorByType(type) {
        switch (type) {
            case "TODO":
                return "red";
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
