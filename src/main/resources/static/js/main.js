/**
 * Created by NAVER on 2017-07-14.
 */


$(document).ready(function () {

    var currentData = {};
    currentData["month"] = $("#current_month").attr("value");

    $.get("http://localhost:9000/request-ical-data", currentData).done(function (eventList) {

        var firstDayOfMonth = getFirstDay($("#current_month").attr("value"));

        for (var i = 0; i < eventList.length; i++) {

            $(findDomLocation(eventList[i], firstDayOfMonth)).append(eventSummary(eventList[i]));
        }
    });
});


function getFirstDay(month) {
    var date = new Date();
    return new Date(date.getFullYear(), month - 1, 1).getDay() - 1;
}

function eventSummary(event) {
    return "<span style='color: blue;'>" + event.summary + "</span>"
}

function findDomLocation(event, firstDayOfMonth) {
    return 'table.schedule_list > tbody > tr:nth-child(2) > td[dayindex=' + (event.date + firstDayOfMonth).toString() + ']';
}


