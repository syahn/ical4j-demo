/**
 * Created by NAVER on 2017-07-20.
 */

(function() {
    $(document).ready(function () {
        $.get("http://localhost:9000/load-iCalData",
            generateMonthObject()
        ).done(function (eventList) {
            console.log(eventList);
            renderingAllEvents(eventList);
        });
    });

    function generateMonthObject() {
        var currentYear = parseInt($("._title").text().toString().substring(0, 5));
        var currentMonth = parseInt($(".print").attr("value"));
        return {
            "month": currentMonth,
            "year": currentYear
        };
    }

    function renderingAllEvents(eventList) {
        for (var i = 0; i<eventList.length; i++) {
            addEventToDom(eventList[i]);
        }
    }

    function addEventToDom(event) {
        var color = selectColorByType(event.type);

        if(event.period>1){

            var startIndex = event.index;
            var tempPeriod = event.period;
            var currentWeekRow = event.weekRow;

            if(startIndex<0){
                tempPeriod+=(startIndex);
                startIndex=0;
            }

            while(tempPeriod!=0){
                $(".table_container div:nth-child("+(currentWeekRow+1)+")>.schedule_list>tbody").append("<tr></tr>");
                for(var i=currentWeekRow*7;i<currentWeekRow*7+7;i++){

                    if(i==startIndex){

                        //만약 일정이 한 주에 이미 모두 꽉 채워지면 다음주로 넘겨야함
                        if(startIndex+tempPeriod-1>=currentWeekRow*7+7){
                            $(".table_container div:nth-child("+(currentWeekRow+1)+")>.schedule_list>tbody>tr:nth-child(3)").append("<td dayindex='" + startIndex + "' colspan = '"+(currentWeekRow*7+7-startIndex)+"'><div style='background: " + color + ";'><span style='color: white;'>" + event.summary + "</span></div></td>");
                            tempPeriod-=(currentWeekRow*7+7-startIndex);// 뿌려줄 남은 기간
                            startIndex=currentWeekRow*7+7;
                            currentWeekRow+=1;
                            break;
                        }
                        //일주일안에 모두 그리기 가능해지면
                        else{
                            $(".table_container div:nth-child("+(currentWeekRow+1)+")>.schedule_list>tbody>tr:nth-child(3)").append("<td dayindex='" + startIndex + "' colspan = '"+tempPeriod+"'><div style='background: " + color + ";'><span style='color: white;'>" + event.summary + "</span></div></td>");
                            i+=(tempPeriod-1);
                            tempPeriod=0;
                        }
                    }else{
                        $(".table_container div:nth-child("+(currentWeekRow+1)+")>.schedule_list>tbody>tr:nth-child(3)").append("<td dayindex='"+i+"' colspan = '1'");
                        // <td dayindex='" + event.index + "' colspan = '"+event.period+"'><div style='background: " + color + ";'><span style='color: white;'>" + event.summary + "</span></div></td>
                    }
                }
            }

        }
        else{
            $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.index + "]")
                .append("<div style='background: " + color + ";'><span style='color: white;'>" + event.summary + "</span></div>");
            $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.index + "]").attr("colspan", event.period);
        }
    }

    function selectColorByType(type) {
        switch(type) {
            case "DAY": return "blue";
            case "YEARLY": return "black";
            case "MONTHLY": return "green";
            case "WEEKLY": return "red";
            case "DAILY": return "MediumPurple";
        }
    }
})();

