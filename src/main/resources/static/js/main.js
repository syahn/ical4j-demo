/**
 * Created by NAVER on 2017-07-14.
 */



$(document).ready(function () {

    $("#create-new-calendar-file").click(function () {
        $.post("http://localhost:8080/create-new-calendar-file")
            .done(function (data) {
                $("#result1").append(data);
            });
    });

    $("#parse-calendar-string").click(function () {
        var icalStrinng = $("#ical_string").val();

        $.post("http://localhost:8080/parse-calendar-string", {
            "ical_string": icalStrinng
        }).done(function (data) {
            $("#result2").append(data);
        });
    });

    $("#parse-calendar-file").click(function () {
        $.post("http://localhost:8080/parse-calendar-file")
            .done(function (data) {
                $("#result3").append(data);
            });
    });


    $("#create-allday-event").click(function () {
        $.post("http://localhost:8080/create-allday-event")
            .done(function (data) {
                $("#result4").append(data);
            });
    });

    $("#create-fourhour-event").click(function () {
        $.post("http://localhost:8080/create-fourhour-event")
            .done(function (data) {
                $("#result5").append(data);
            });
    });

    $("#create-new-event").click(function () {
        var event = $("#event").val();
        var date = $("#date").val();

        $.post("http://localhost:8080/create-new-event", {
            "event": event,
            "date": date
        }).done(function (data) {
            $("#result6").append(
                "<h3>event: " + data.event + "</h3>" +
                "<h3>date: " + data.date + "</h3>" +
                "<p>iCal: </p>" +
                "<p>" + data.iCal + "</p>");
        });
    });
    $("#parse-meeting-event").click(function () {
        var event = $("#ical_input_string").val();

        $.post("http://localhost:8080/parse-meeting-event", {
            "ical_string": event
        }).done(function (data) {
            $("#result-location").append("<span>" + data.location + "</span>");
            $("#result-summary").append("<span>" + data.summary + "</span>");
            $("#result-time").append("<span>" + data.start + ' - ' + data.end + "</span>");
        });
    });


});