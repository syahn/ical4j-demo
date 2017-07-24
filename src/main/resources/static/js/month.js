/**
 * Created by NAVER on 2017-07-20.
 */


$(document).ready(function () {
    $.get("http://localhost:8080/load-iCalData",
        generateMonthObject()
    ).done(function (eventList) {
        renderingAllEvents(eventList);
    });
});


function renderingAllEvents(list) {
    for (var i = 0; i < list.length; i++) {
        var event = list[i];
        if (event.recur === false) {
            addEventToDom(event.startIndex, event.summary, "blue");
        }
        else {
            console.log(event);

            recurEventToDom(event);
        }
    }
}

function recurEventToDom(event) { // 요일 반복에 대한 고려해야함

    var end = event.untilDate === null ? 42 : event.endIndex + 1;

    if (event.frequency === "DAILY") {

        for (j = event.startIndex; j < end;) {
            addEventToDom(j, event.summary, 'MediumPurple');
            j += event.interval;
        }
    }
    else if (event.frequency === "WEEKLY") {




        var startDayList = event.startDayList;

        for(d=0;d<startDayList.length;d++){
            var diff = startDayList[d]-event.startDayNum;
            if(diff<0){
                diff+=7; // (ex 수,일 반복인데 수요일부터 시작일 경우)
            }
            for(j=event.startIndex;j<end;){
                if(!(j+diff>=end)){
                    addEventToDom(j+diff,event.summary,'red');
                }
                j+=event.interval*7;
            }
        }

    }
    else if (event.frequency === "MONTHLY") {

        var tempMonth = event.startMonth;
        var tempYear = event.startYear;
        var tempCount = 0;

        for (j = event.startIndex; j < end;) {

            if (tempCount == event.interval || tempCount == 0) {
                addEventToDom(j, event.summary, 'green');
                tempCount = 0;
            }

            var daysForInterval = daysOfMonth(tempYear, tempMonth);
            j += daysForInterval;
            tempMonth++;
            tempCount++;

            if (tempMonth > 12) {
                tempMonth = 1;
                tempYear++;
            }
        }
    }
    else if (event.frequency === "YEARLY") {

        var tempYear = event.startYear;
        var tempCount = 0;
        console.log("clicked", event, end);
        for (j = event.startIndex; j < end;) {

            if (tempCount == event.interval || tempCount == 0) {
                console.log("clicked", event);
                addEventToDom(j, event.summary, 'black');
                tempCount = 0;
            }

            var daysForInterval = daysOfYear(tempYear);
            j += daysForInterval;
            tempYear++;
            tempCount++;
        }
    }

}

function generateMonthObject() {
    var currentYear = parseInt($("._title").text().toString().substring(0, 5));
    var currentMonth = parseInt($(".print").attr("value"));
    return {
        "month": currentMonth,
        "year": currentYear
    };
}

//이벤트 추가
function addEventToDom(index, summary, color) {
    $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + index + "]")
        .append("<div style='background: " + color + ";'><span style='color: white;'>" + summary + "</span></div>");
}

// y년도 m월의 일수 계산
function daysOfMonth(year, month) {
    return parseInt(new Date(year, month, 0).getDate());
}

// y년도 일수 계산
function daysOfYear(year) {
    return isLeapYear(year) ? 366 : 365;
}

// 윤년
function isLeapYear(year) {
    return year % 400 === 0 || (year % 100 !== 0 && year % 4 === 0);
}