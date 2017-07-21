/**
 * Created by NAVER on 2017-07-14.
 */


$(document).ready(function () {
    var currentPageData = generateMonthObject();
    $.get("http://localhost:9000/request-ical-data", currentPageData)
        .done(function (eventList) {
            for (var i = 0; i < eventList.length; i++) {
                var event = eventList[i];
                if (shouldNotIncluded(event, currentPageData.month)) continue;
                if (isRecurrentEvent(event)) {
                    appendRecurrentEvent(event, currentPageData.month);
                } else {
                    $(findDomLocation(event, currentPageData.month)).append(eventSummary(event));
                }
            }
        });
});


function shouldNotIncluded(event, currentMonth) {

    if (event.recur && currentMonth <= event.untilMonth) {
        return (event.month > currentMonth && event.startDate > calcuateAlloc("next"))
            ? true
            : false;
    }
    if (event.month < currentMonth) {
        var lastDate = getLastDate(event.month);
        if (event.startDate < lastDate - calcuateAlloc("previous")) {
            return true;
        }
    }
    else if (event.month > currentMonth) {
        if (event.startDate > calcuateAlloc("next")) {
            return true;
        }
    }

    return false;
}

function appendRecurrentEvent(event, currentMonth) {

    while (event.month < event.untilMonth || (event.month === event.untilMonth && event.startDate <= event.untilDate)) {
        // if (event.month === currentMonth) {
        $(findDomLocation(event, currentMonth)).append(eventSummary(event));
        // }


        if (event.frequency == "DAILY") {
            event.startDate += event.interval;
            if (event.startDate > getLastDate(event.month)) {
                event.month += 1;
                event.startDate = event.startDate - getLastDate(event.month);
            }

        }
        else if (event.frequency == "WEEKLY") {
            event.startDate += event.interval * 7;
            if (event.startDate > getLastDate(event.month)) {
                event.month += 1;
                event.startDate = event.startDate - getLastDate(event.month);
            }
        }
        else if (event.frequency == "MONTHLY") {
            event.month += 1;

        }

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
    var firstDayOfMonth = getFirstDay(currentMonth);

    // Process this month
    if (whichMonth == currentMonth) {
        index = event.startDate + firstDayOfMonth - 1;
    } // Process previous month
    else if (whichMonth < currentMonth) {
        var firstDateIncluded = getLastDate(whichMonth) - calcuateAlloc("previous") + 1;
        index = event.startDate - firstDateIncluded;
    } // Process next month
    else {

        index = firstDayOfMonth + getLastDate(currentMonth) + event.startDate - 1;
    }

    return index.toString();
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
