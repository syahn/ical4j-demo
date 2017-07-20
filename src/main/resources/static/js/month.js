/**
 * Created by NAVER on 2017-07-20.
 */

//2월의 경우 따로 생각해봐야함
var month_30 = [4,6,9,11]; // 30일인 달

$(document).ready(function () {

    var month = $(".print").attr("value");

    $.post("http://localhost:8080/apply-iCalData",
        {
            "month": month
        }
        ).done(function (list) {

            //첫번째로 1일이 나오는 dayindex 찾기 - 해당 페이지의 월 기준(현재 달)
            var moveDayViewlist = $("._move_day_view");
            var indexOfFirstDay = null;
            for(j=0;j<moveDayViewlist.length;j++){

                var dayNum = moveDayViewlist[j].innerHTML;
                if(dayNum==1){
                    indexOfFirstDay = parseInt(moveDayViewlist[j].getAttribute("dayindex"));
                    break;
                }
            }
            console.log(indexOfFirstDay);

            var list_6 = list[0];
            //현재달 이벤트 리스트 집어넣기
            for(i=0;i<list_6.length;i++){
                var date = list_6[i].start.toString().substring(6,8);
                console.log(list_6[i].start);
                var summary = list_6[i].summary.toString();
                //td태그의 dayindex 찾아서 맞는 날짜에 집어넣기(실제 이번트 정보 들어가는 요소)
                var num = (parseInt(date)+indexOfFirstDay-1).toString();
                var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+num+"]");
                dateContainer.append("<span>"+summary+"</span>");

            }

            //다음달 이벤트들
            var nextMonthFirstIndex;
            //달마다 플러스하는 숫자 다름 - 더 간단한 코드로 구현하기
            if($.inArray("6", month_30)!=null){
                nextMonthFirstIndex = indexOfFirstDay+30;
            }
            else{
                nextMonthFirstIndex = indexOfFirstDay+31;
            }

            var list_7 = list[1];
            for(i=0;i<list_7.length;i++){
                var date = list_7[i].start.toString().substring(7,8);//무조건 한자리 수
                console.log(list_7[i].start);
                var summary = list_7[i].summary.toString();
                //td태그의 dayindex 찾아서 맞는 날짜에 집어넣기(실제 이번트 정보 들어가는 요소)
                var num = (parseInt(date)+nextMonthFirstIndex-1).toString();
                var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+num+"]");
                dateContainer.append("<span>"+summary+"</span>");

            }

            //전 달 이벤트들
            var prevNum; // 만약 현재 달의 1일이 시작이 아니라면
            //이전달이 30일, 31일 ,28일 인가에 따라 계산법 달라짐
            if($.inArray("6", month_30)!=null){
                prevNum = indexOfFirstDay-31;
            }
            else{
                prevNum = indexOfFirstDay-32;
            }

            var list_prev = list[2];
            for(i=0;i<list_prev.length;i++){
                var date = list_prev[i].start.toString().substring(7,8);//무조건 한자리 수
                console.log(list_prev[i].start);
                var summary = list_prev[i].summary.toString();
                //td태그의 dayindex 찾아서 맞는 날짜에 집어넣기(실제 이번트 정보 들어가는 요소)
                var num = (parseInt(date)+prevNum).toString();
                var dateContainer = $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex="+num+"]");
                dateContainer.append("<span>"+summary+"</span>");

            }

        });
});