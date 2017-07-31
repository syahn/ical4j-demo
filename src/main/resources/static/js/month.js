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
        if (event.period > 1) {
            appendEvent("row", event);

            var offset = event.index - event.weekRow * 7;
            for (var i = 0; i < offset; i++) {
                appendEvent("blank", event);
            }
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
            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(3)")
                .append("<td dayindex='" + event.index + "' colspan = '" +
                    event.period + "'>" +
                    "<div style='background: " + color + ";'>" +
                    "<span style='color: white;'>" +
                    event.summary +
                    "</span></div></td>"
                );
        } else if (type === "blank") {
            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody>tr:nth-child(3)")
                .append("<td dayindex='" + event.index + "' colspan = '1'>&nbsp;</td>");
        } else if (type === "oneday") {
            $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.index + "]")
                .append("<div style='background: " + color + ";'>" +
                    "<span style='color: white;'>" +
                    event.summary +
                    "</span></div>");
            $(".schedule_list>tbody>tr:nth-child(2)>td[dayindex=" + event.index + "]")
                .attr("colspan", event.period);
        } else {
            $(".table_container div:nth-child(" + (weekRow + 1) + ")>.schedule_list>tbody")
                .append("<tr></tr>");
        }
    }
})();

