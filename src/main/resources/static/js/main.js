/**
 * Created by NAVER on 2017-07-14.
 */

$(document).ready(function () {

    //이벤트 추가 저장
    $("#addData").click(function () {
        var eventSummary = $("#eventSummary").val();
        var startDate = $("#startDate").val();
        var endDate = $("#endDate").val();

        $.post("http://localhost:8080/add",
            {
                "eventSummary": eventSummary,
                "startDate": startDate,
                "endDate": endDate

            }).done(function () {
            $("#post").append("<h3>summary: " + eventSummary + "</h3>"
                + "<h3>startDate: " + startDate + "</h3>"
                + "<h3>endDate: " + endDate + "</h3>"
                + "<h3>========</h3>");
        });
    });

    $("#create-new-calendar-file").click(function () {
        $.post("http://localhost:8080/create-new-calendar-file")
            .done(function (data) {
                $("#result1").append(data);
            });
    });

    $("#parse-calendar-string").click(function () {
        $.post("http://localhost:8080/parse-calendar-string")
            .done(function (data) {
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


});