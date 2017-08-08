/**
 * Created by NAVER on 2017-07-14.
 */

(function () {
    var previewButton = document.getElementById("print-btn");

    previewButton.addEventListener("click", function (e) {
        var month = e.target.value;
        var year = 2017;
        //메인 페이지 인쇄 리퀘스트
       $.post("/make-preview", {
            startMonth: month,
            endMonth : month,
            currentYear: 2017//임시
        }).done(function () {
            //openPreviewTap(month);
           openWindowWithPost(month,year);
           document.getElementById('TheForm').submit();
        });
    });

    function openPreviewTap(month) {

        // currentmonth, currentyear parameter passing?
       window.open('/preview', '인쇄 프리뷰', 'resizable=1,width=526,height=715');

    }

    function openWindowWithPost(month, year) {
        var f = document.getElementById('TheForm');
        f.month.value = month;
        f.year.value = year;
        window.open('', 'Preview', 'resizable=1,width=526,height=715');
        f.submit();
    }

})();
