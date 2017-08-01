/**
 * Created by NAVER on 2017-07-20.
 */

(function () {
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
        for (var i = 0; i < eventList.length; i++) {
            addEventToDom(eventList[i]);
        }
    }

    function addEventToDom(event) {
        if (event.type==="PERIOD") {
            appendEvent("period", event);
        }
        else {
            appendEvent("oneday", event);
        }
    }

    function selectColorByType(type) {
        switch (type) {
            case "DAY":
                return "blue";
            case "YEARLY":
                return "black";
            case "MONTHLY":
                return "green";
            case "WEEKLY":
                return "red";
            case "PERIOD":
                return "grey";
            case "DAILY":
                return "MediumPurple";
        }
    }

    function appendEvent(type, event) {
        var color = selectColorByType(event.type);
        var weekRow = event.weekRow;

        if (type === "period") {
            appendEvent("row", event);
            //해당 인덱스 자리에 삽입 후 period만큼 기간 확장
            for(j=weekRow*7;j<weekRow*7+7;j++) {
                if(event.index==j){
                    $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(3)")
                        .append("<td dayindex='" + event.index + "' colspan = '" +
                            event.period + "'>" +
                            "<div style='background: " + color + ";'>" +
                            "<span style='color: white;'>" +
                            event.summary +
                            "</span></div></td>"
                        );
                    j+=(event.period-1);
                }else{
                    //빈공간 &nbsp부여
                    $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(3)").append("<td dayindex='" + j + "' colspan = '1'>&nbsp;</td>");
                }
            }

        } else if (type === "oneday") {

            console.log(event.summary+(weekRow+1));
            var exist=false;
            var tempLocation;
            var lastLine=1;
            for(i=0;i<5;i++) {
                //tr 존재하고 빈칸 있을경우 - 마지막 tr 기준
                if ($(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child("+i+")").length != 0) {
                    exist=false;
                    if($(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child("+i+")>td[dayindex=" + event.index + "]").html()=="&nbsp;"){
                        tempLocation = i;
                        exist=true;
                        break;// - 상위 2줄이 모두 &nbsp면 가장 위의 줄로 가야하므로 break;!
                    }
                    lastLine++;
                }
            }

            if(exist){ // 빈공간 존재시
                $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child("+tempLocation+")>td[dayindex=" + event.index + "]").empty();
                $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child("+tempLocation+")>td[dayindex=" + event.index + "]").append("<div style='background: " + color + ";'>" +
                    "<span style='color: white;'>" +
                    event.summary +
                    "</span></div>");
                return;
            }

            // for(i=0;i<5;i++){
                //tr4 존재 안하면 생성
                if($(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child("+lastLine+")").length==0){
                    appendEvent("row", event);
                    //dayIndex부여한 tr로 갱신
                    for(j=weekRow*7;j<weekRow*7+7;j++) {
                        $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child("+lastLine+")").append("<td dayindex='" + j + "' colspan = '1'>&nbsp;</td>");
                    }
                }
           // }

            //&nbsp지우고 넣어야 css 깔끔
            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child("+lastLine+")>td[dayindex=" + event.index + "]").empty();
            $(".schedule_list>tbody>tr:nth-child("+lastLine+")>td[dayindex=" + event.index + "]")
                .append("<div style='background: " + color + ";'>" +
                    "<span style='color: white;'>" +
                    event.summary +
                    "</span></div>");
            $(".schedule_list>tbody>tr:nth-child("+lastLine+")>td[dayindex=" + event.index + "]")
                .attr("colspan", 1);
        } else {
            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody")
                .append("<tr></tr>");
        }
    }
})();

