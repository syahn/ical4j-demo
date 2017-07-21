/**
 * Created by NAVER on 2017-07-14.
 */


$(document).ready(function () {
    var currentPageData = generateMonthObject();
    $.get("http://localhost:9000/request-ical-data", currentPageData)
        .done(function (eventList) {
            for (var i = 0; i < eventList.length; i++) {
                var event = eventList[i];
                console.log(event);
                if (isRecurrentEvent(event)) {
                    appendRecurrentEvent(event, currentPageData.month);
                } else {
                    $(findDomLocation(event, currentPageData.month)).append(eventSummary(event));
                }

            }
        });
});

function appendRecurrentEvent(event, currentMonth) {
    var numOfCount = event.untilDate - event.startDate + 1;
    for (var i = 0; i<numOfCount; i++) {
        $(findDomLocation(event, currentMonth)).append(eventSummary(event));
        event.startDate += event.interval;
    }

}

function isRecurrentEvent(event) {
    return event.recur ? true : false;
}

function generateMonthObject() {
    var currentData = {};
    currentData["month"] = $("#current_month").attr("value");
    return currentData;
}

function eventSummary(event) {
    return "<span style='color: blue;'>" + event.summary + "</span>"
}

function findDomLocation(event, currentMonth) {
    return '.schedule_list > tbody > tr:nth-child(2) > td[dayindex=' + getLocation(event, currentMonth) + ']';
}

function getLocation(event, currentMonth) {
    var index;
    var whichMonth = event.month;
    var firstDayOfMonth = getFirstDay($("#current_month").attr("value")) - 1;

    if (whichMonth == currentMonth) {
        index = event.startDate + firstDayOfMonth;


    } // 이전 달 이벤트
    else if (whichMonth < currentMonth) {
        var firstDateIncluded = getLastDate(whichMonth) - calcuateAlloc(whichMonth);
        index = event.startDate - firstDateIncluded;

    } // 다음 달 이벤트
    else {
        index = firstDayOfMonth + getLastDate($("#current_month").attr("value")) + event.startDate;
    }

    return index.toString();
}

function shouldIncluded(event, whichMonth) {
    if (whichMonth == "previous") {
        var lastDate = getLastDate(event.month);

        if (event.startDate >= lastDate - calcuateAlloc(whichMonth)) {
            return true;
        }
    }
    else {
        if (event.startDate <= calcuateAlloc(whichMonth)) {
            return true;
        }
    }
    return false;
}

function calcuateAlloc(whichMonth) {
    if (whichMonth == "previous") {
        return getFirstDay($("#current_month").attr("value"));
    }
    else {
        return 6 - getLastDay($("#current_month").attr("value"))
    }
}

function getFirstDay(month) {
    return new Date(new Date().getFullYear(), month - 1, 1).getDay();
}

function getLastDay(month) {
    return new Date(new Date().getFullYear(), month, 0).getDay();
}

function getFirstDate(month) {
    return new Date(new Date().getFullYear(), month - 1, 1).getDate();
}

function getLastDate(month) {
    return new Date(new Date().getFullYear(), month, 0).getDate();
}
