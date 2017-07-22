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

            //현재 달의 1일 dayindex 찾기
            var indexOfFirstDay = parseInt($("table[data-week-row='0']>tbody>tr[class='date']>td>strong:not(:contains('31')):contains(1)").attr("dayindex"));

            for(i=0;i<list.length;i++){

                var event = list[i];

                //1. 반복없는 데이터
                if(event.isRecur==false){
                    addEventToDom(event.startDay,event.startMonth,event.startYear,event.summary,indexOfFirstDay);
                }

                //2. 반복있는 데이터
                else{

                    //1-무한 반복
                    if(event.until==null){

                        if(event.frequency==="DAILY"){

                            //렌더링 시작할 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            for(j=startIndex;j<42;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval;
                            }
                        }
                        else if(event.frequency==="WEEKLY"){
                            //렌더링 시작할 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            for(j=startIndex;j<42;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval*7;
                            }
                        }
                        else if(event.frequency==="MONTHLY"){
                            //렌더링 시작할 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            for(j=startIndex;j<42;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval*31;//월마다 간격 다른 것으로 수정해야함
                            }
                        }
                        else if(event.frequency==="YEARLY"){
                            //렌더링 시작할 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            for(j=startIndex;j<42;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval*365;//월마다 간격 다른 것으로 수정해야함
                            }
                        }
                    }

                    //2-기간 반복
                    else {
                        if(event.frequency==="DAILY"){

                            //렌더링 시작 및 종료 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            var endIndex = parseInt(calculateIndexOfDate(event.untilDay,event.untilMonth,event.untilYear,indexOfFirstDay));
                            for(j=startIndex;j<endIndex+1;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval;
                            }
                        }
                        else if(event.frequency==="WEEKLY"){
                            //렌더링 시작할 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            var endIndex = parseInt(calculateIndexOfDate(event.untilDay,event.untilMonth,event.untilYear,indexOfFirstDay));
                            for(j=startIndex;j<endIndex+1;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval*7;
                            }
                        }
                        else if(event.frequency==="MONTHLY"){
                            //렌더링 시작할 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            var endIndex = parseInt(calculateIndexOfDate(event.untilDay,event.untilMonth,event.untilYear,indexOfFirstDay));
                            for(j=startIndex;j<endIndex+1;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval*31;//월마다 간격 다른 것으로 수정해야함
                            }
                        }
                        else if(event.frequency==="YEARLY"){
                            //렌더링 시작할 절대 index찾아서 시작
                            var startIndex = parseInt(calculateIndexOfDate(event.startDay,event.startMonth,event.startYear,indexOfFirstDay));
                            var endIndex = parseInt(calculateIndexOfDate(event.untilDay,event.untilMonth,event.untilYear,indexOfFirstDay));
                            for(j=startIndex;j<endIndex+1;){
                                addRecurEventToDom(j,event.summary);
                                j+=event.interval*365;//월마다 간격 다른 것으로 수정해야함
                            }
                        }
                    }

                }
            }
        });
});


//종일이벤트 추가
function addEventToDom(eventDay,eventMonth,eventYear,summary,firstIndex) {

        var numOfIndex = calculateIndexOfDate(eventDay,eventMonth,eventYear,firstIndex);

        var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+numOfIndex+"]");
        dateContainer.append("<div style='background: blue;'><span style='color: white;'>" + summary + "</span></div>");
}

//반복이벤트 추가
function addRecurEventToDom(index,summary){

    var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+index+"]");
    dateContainer.append("<div style='background: red;'><span style='color: white;'>" + summary + "</span></div>");

}

//이벤트의 절대 인덱스 계산법
function calculateIndexOfDate(eventDay, eventMonth, eventYear,firstIndex){

    var index;
    var tempMonth = currentMonth;
    var tempYear = currentYear;

    //연도도 매칭 시켜야함
    if(eventMonth==currentMonth){
        index = (eventDay+firstIndex-1).toString();
    }else if(eventMonth>currentMonth){
        index = (eventDay+lastday(currentYear,currentMonth)+firstIndex-1).toString();
    }else if(eventMonth<currentMonth){

        while(tempYear>=eventYear&&tempMonth>eventMonth){

            if(tempMonth==0){
                tempMonth=12;
                tempYear--;
            }

            eventDay-=lastday(tempYear,tempMonth-1);
            tempMonth--;

        }
        index = (eventDay+firstIndex-1).toString();
    }

    return index;
}

//월별 총일수 계산
function lastday(y,m){
    var m = m==0 ? 12 : m; //1월일 때 이전 달 조정
    var y = m==0 ? y-1 : y; //1월이면 작년 12월이 이전 달이므로 년도 조정
    return  parseInt(new Date(y, m, 0).getDate());
}