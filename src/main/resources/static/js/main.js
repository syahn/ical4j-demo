/**
 * Created by NAVER on 2017-07-14.
 */


$(document).ready(function () {
    $.get("http://localhost:9000/request-ical-data",
        generateMonthObject()
    ).done(function(events) {
        renderingCalendarEvents(events);
    });
});

function renderingCalendarEvents(events) {
    for (var i = 0; i < events.length; i++) {

        var event = events[i];
        var currentMonth = $("#current_month").attr("value");
        var indexOfDom = getLocation(event, currentMonth);

        if (shouldNotIncluded(event, currentMonth, indexOfDom))
            continue;

        if (isRecurrentEvent(event)) {
            appendRecurrentEvent(event, currentMonth);
        } else {
            $(findDomLocation(indexOfDom)).append(eventSummary(event));
        }
    }
}


function shouldNotIncluded(event, currentMonth, index) {

    // Does recurrent event have a relation with current month?
    if (isValidRecurrentEvent(event, currentMonth)) {
        return true;
    } else {
        return false;
    }
    // Is is a event in previous month?
    if (event.month < currentMonth) {
        var lastDate = getLastDate(event.month);
        if (event.startDate < lastDate - calcuateAlloc("previous")) {
            return true;
        }
    }
    // Is is a event in next month?
    if (event.month > currentMonth) {
        if (event.startDate > calcuateAlloc("next")) {
            return true;
        }
    }
    // Is index is in the grid of current month?
    if (index < 0 && index > 34) return true;

    return false;
}

function appendRecurrentEvent(event, currentMonth) {

    while (event.month < event.untilMonth || (event.month === event.untilMonth && event.startDate <= event.untilDate)) {

        $(findDomLocation(getLocation(event, currentMonth))).append(eventSummary(event));

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

function isValidRecurrentEvent(event, currentMonth) {
    if (event.month > currentMonth && event.startDate > calcuateAlloc("next")) {
        return event.recur && currentMonth <= event.untilMonth;
    }
    return false;
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

    return index;
}

function generateMonthObject() {
    var currentData = {};
    currentData["month"] = $("#current_month").attr("value");
    return currentData;
}

function isRecurrentEvent(event) {
    return event.recur ? true : false;
}

function eventSummary(event) {
    return "<div style='background: blue;'><span style='color: white;'>" + event.summary + "</span></div>"
}

function findDomLocation(index) {
    return '.schedule_list > tbody > tr:nth-child(2) > td[dayindex=' + index + ']';
}

function calcuateAlloc(whichMonth) {
    if (whichMonth == "previous") {
        return getFirstDay($("#current_month").attr("value"));
    }
    if (whichMonth == "next") {
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
