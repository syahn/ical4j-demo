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
            //1. 해당 인덱스의 weekrow 찾기

        
            $(".table_container div:nth-child("+(event.weekRow+1)+")>.schedule_list>tbody").append("<tr></tr>");
            for(var i=event.weekRow*7;i<event.weekRow*7+7;i++){
                console.log(event.index);
                if(i==event.index){
                    console.log(event.index);
                    $(".table_container div:nth-child("+(event.weekRow+1)+")>.schedule_list>tbody>tr:nth-child(3)").append("<td dayindex='" + event.index + "' colspan = '"+event.period+"'><div style='background: " + color + ";'><span style='color: white;'>" + event.summary + "</span></div></td>");
                    i+=(event.period-1);
                }else{
                    $(".table_container div:nth-child("+(event.weekRow+1)+")>.schedule_list>tbody>tr:nth-child(3)").append("<td dayindex='"+i+"' colspan = '1'");
                // <td dayindex='" + event.index + "' colspan = '"+event.period+"'><div style='background: " + color + ";'><span style='color: white;'>" + event.summary + "</span></div></td>
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

