/**
 * Created by NAVER on 2017-07-20.
 */
var year = null ;
var month = null;

$(document).ready(function () {

    year = parseInt($("._title").text().toString().substring(0,5));
    month = $(".print").attr("value");

    $.post("http://localhost:8080/apply-iCalData",
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

function filterMonthList(list,indexNum,type) {

    for(i=0;i<list.length;i++){
        var date = list[i].start.toString().substring(6,8);
        var summary = list[i].summary.toString();

        var num = calculateIndex(date,indexNum,type);

        var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+num+"]");
        dateContainer.append("<span>"+summary+"</span>");

    }

}

function calculateIndex(date, firstIndex, type){

    var index;

    var lastday = function(y,m){
        var m = m==0 ? 12 : m; //1월일 때 이전 달 조정
        return  new Date(y, m, 0).getDate();
    }

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