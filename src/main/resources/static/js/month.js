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

        $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.index + "]")
            .append("<div style='background: " + color + ";'><span style='color: white;'>" + event.summary + "</span></div>");
        $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.index + "]").attr("colspan", event.period);
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

