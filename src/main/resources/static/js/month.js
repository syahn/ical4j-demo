/**
 * Created by NAVER on 2017-07-20.
 */
var currentYear = null ;
var currentMonth = null;

$(document).ready(function () {

    currentYear = parseInt($("._title").text().toString().substring(0,5));
    currentMonth = parseInt($(".print").attr("value"));

    $.post("http://localhost:8080/apply-iCalData",
        {
            "month": currentMonth,
            "year": currentYear
        }
        ).done(function (list) {

            renderingAllEvents(list);

        });
});

function renderingAllEvents(list) {

    //현재 달의 1일 dayindex 찾기 - 월뷰에 표시할 각 데이터의 절대 인덱스 계산에 기준점
    var indexOfFirstDay = parseInt($("table[data-week-row='0']>tbody>tr[class='date']>td>strong:not(:contains('31')):contains(1)").attr("dayindex"));

    for(i=0;i<list.length;i++){

        var event = list[i];

        //이벤트의 시작날짜의 절대 인덱스
        var startIndex = calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay);

        //1. 반복없는 데이터
        if(event.isRecur==false){
            addEventToDom(startIndex,event.summary,"purple");
        }

        //2. 반복있는 데이터
        else{

            //1 - 무한 반복
            if(event.until==null){
                recurEventToDom(event,startIndex,42);
            }

            //2 - 기간 반복
            else {
                //이벤트 종료날짜의 절대인덱스
                var endIndex = calculateIndexOfDate(event.untilDay,event.untilMonth,event.untilYear,indexOfFirstDay);
                recurEventToDom(event,startIndex,endIndex);
            }

        }
    }
}

function recurEventToDom(event,startIndex,endIndex){ // 요일 반복에 대한 고려해야함

    var end = endIndex>=42 ? 42 : endIndex+1;

    if(event.frequency==="DAILY"){

        for(j=startIndex;j<end;){
            addEventToDom(j,event.summary,'black');
            j+=event.interval;
        }
    }
    else if(event.frequency==="WEEKLY"){

        for(j=startIndex;j<end;){
            addEventToDom(j,event.summary,'red');
            j+=event.interval*7;
        }
    }
    else if(event.frequency==="MONTHLY"){

        var tempMonth = event.startMonth;
        var tempYear = event.startYear;

        for(j=startIndex;j<end;){

            addEventToDom(j,event.summary,'green');
            var daysForInterval = daysOfMonth(tempYear,tempMonth);
            j+=event.interval*daysForInterval;
            tempMonth++;

            if(tempMonth>12){
                tempMonth = 1;
                tempYear++;
            }
        }
    }
    else if(event.frequency==="YEARLY"){

        var tempYear = event.startYear;

        for(j=startIndex;j<end;){
            console.log(j);
            addEventToDom(j,event.summary,'blue');
            var daysForInterval = daysOfYear(tempYear);
            j+=event.interval*daysForInterval;
            tempYear++;
        }
    }

}


//이벤트 추가
function addEventToDom(index,summary,color){

    var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+index+"]");
    dateContainer.append("<div style='background: "+color+";'><span style='color: white;'>" + summary + "</span></div>");

}

//이벤트의 절대 인덱스 계산법
function calculateIndexOfDate(eventStartDay, eventStartMonth, eventStartYear,firstIndex){

    var index;
    // var currentDate = parseInt(currentYear.toString()+currentMonth.toString());
    // var eventStartDate = parseInt(eventStartYear.toString()+eventStartMonth.toString());

    //현재 달에서 이벤트 시작시
    if(eventStartMonth==currentMonth&&eventStartYear==currentYear){

        index = eventStartDay+firstIndex-1;

    }
    //현재 달 이후에 이벤트 시작시(최대 6일까지만 존재)
    else if((eventStartMonth>currentMonth&&eventStartYear==currentYear)||eventStartYear>currentYear){

        index = eventStartDay+firstIndex-1+daysOfMonth(currentYear,currentMonth);

    }
    //현재 달 이전에 이벤트가 시작시(얼마나 이전인지 한계 없음)
    else{

        var tempMonth = currentMonth;
        var tempYear = currentYear;

        while(tempYear>eventStartYear||(tempYear==eventStartYear&&tempMonth>eventStartMonth)){

            eventStartDay-=daysOfMonth(tempYear,tempMonth-1);
            tempMonth--;

            if(tempMonth==0){
                tempMonth=12;
                tempYear--;
            }
        }

        index = eventStartDay+firstIndex-1;
    }

    return index;
}

//y년도 m월의 일수 계산
function daysOfMonth(year,month){
    return  parseInt(new Date(year, month, 0).getDate());
}

//y년도 일수 계산
function daysOfYear(year)
{
    return isLeapYear(year) ? 366 : 365;
}

//윤년
function isLeapYear(year) {
    return year % 400 === 0 || (year % 100 !== 0 && year % 4 === 0);
}