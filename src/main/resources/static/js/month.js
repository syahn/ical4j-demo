/**
 * Created by NAVER on 2017-07-20.
 */
var year = null ;
var month = null;

$(document).ready(function () {

    year = parseInt($("._title").text().toString().substring(0,5));
    month = $(".print").attr("value");

    $.post("http://localhost:9000/apply-iCalData",
        {
            "month": month,
            "year": year
        }
        ).done(function (list) {

            //현재 달의 1일 dayindex 찾기
            var indexOfFirstDay = parseInt($("table[data-week-row='0']>tbody>tr[class='date']>td>strong:not(:contains('31')):contains(1)").attr("dayindex"));

            //현재 달 필터링 후 셀 안에 표시
            filterMonthList(list[0],indexOfFirstDay,0);

            //다음 달 필터링 후 셀 안에 표시
            filterMonthList(list[1],indexOfFirstDay,1);

            //이전 달 필터링 후 셀 안에 표시
            filterMonthList(list[2],indexOfFirstDay,2);

        });
});

//데이터 필터링
function filterMonthList(list,indexNum,type) {

    for(i=0;i<list.length;i++){
        //종일데이터의 경우
        var startDate = list[i].start.toString().substring(6,8);
        var endDate = list[i].end.toString().substring(6,8);//종일만적용됨
        var summary = list[i].summary.toString();

        //반복데이터의경우
        var interval = list[i].interval;
        var frequency = list[i].frequency;
        var dayList = list[i].bydayList; // weekly의 경우 필수적으로 포함됨
        var until = list[i].until;
        var monthOfStartDate = list[i].start.toString().substring(4,6);
        var yearOfStartDate = list[i].start.toString().substring(0,4);

        addEventToDom(startDate,summary,indexNum,type);
    }
}

function filterRecurMonthList() {
    
}

//이벤트추가
function addEventToDom(startDate,summary,indexNum,type) {
    var numOfIndex = calculateIndex(startDate,indexNum,type);
    var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+numOfIndex+"]");
    dateContainer.append("<span style='color:blue'>"+summary+"</span>");
}

function calculateIndex(date, firstIndex, type){

    var index;

    //currentmonth
    if(type===0){
        index = (parseInt(date)+firstIndex-1).toString();
    }
    //nextmonth
    else if(type===1){
        index = (parseInt(date)+parseInt(lastday(year,month))+firstIndex-1).toString();
    }
    //premonth
    else if(type===2){
        index = (parseInt(date)-parseInt(lastday(year,month-1))+firstIndex-1).toString();
    }

    return index;
}

function lastday(y,m){
    var m = m==0 ? 12 : m; //1월일 때 이전 달 조정
    var y = m==0 ? y-1 : y; //1월이면 작년 12월이 이전 달이므로 년도 조정
    return  new Date(y, m, 0).getDate();
}



// //반복데이터일 경우
// if(interval!==null){
//     //정해진기간 존재하는 경우
//     if(until!==null){
//         var ReCurEndDate = until.substring(6,8);//반복 데이터의 경우 마지막 종료일
//         var monthOfEndDate = until.substring(4,6);
//     }
//     //무한 반복
//     else{
//         if(frequency === "YEARLY"){
//             if((year-parseInt(yearOfStartDate))%interval!==0)
//             {
//                 continue;
//             }
//             console.log(summary);
//         }else if(frequency === "MONTHLY") {
//             if ((month - parseInt(monthOfStartDate)) % interval !== 0) {
//                 continue;
//             }
//             console.log(summary);
//         }else if(frequency === "WEEKLY"){
//
//             var tempMonth = month;
//             var tempstartDate = parseInt(startDate);
//             var j = 0;
//             // while(true){
//             //     tempDate = (parseInt(startDate)+interval*7*j).toString();
//             //     addEventToDom(tempDate,summary,indexNum,type);
//             //     j++;
//             // }
//             for(j=0;j<10;j++){
//                 //이벤트의 시작일자와 해당 월의 시작 일자 겹침 방지
//                 tempDate = (parseInt(startDate)+interval*7*j).toString();
//                 addEventToDom(tempDate,summary,indexNum,type);
//             }
//             console.log(summary);
//
//         }else if(frequency === "DAILY"){
//
//         }
//     }
// }