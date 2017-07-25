/**
 * Created by NAVER on 2017-07-14.
 */

(function() {
    var previewButton = document.getElementById("print-btn");

    previewButton.addEventListener("click", function (e) {
        var month = e.target.value;
        makeDummyWindow(month);
        takeScreenShot(month);
    });

    function makeDummyWindow(month) {

        var hiddenFrame = document.createElement("iframe");
        hiddenFrame.setAttribute("id", "hiddenFrame");
        hiddenFrame.setAttribute("width", "100%");
        hiddenFrame.setAttribute("height", "100%");
        hiddenFrame.setAttribute("frameBorder", "0");
        document.body.appendChild(hiddenFrame);

        $("#hiddenFrame").attr("src", generateNewUrl(month));
    }

    function generateNewUrl(month) {
        return "http://localhost:9000/month/" + month;
    }

    function takeScreenShot(month) {
        html2canvas(document.getElementById("hiddenFrame"), {
            onrendered: function (canvas) {
                // 캔버스 URL 추출
                var dataUrl = canvas.toDataURL();

                // 추출한 URL을 서버에 저장한 후, 프리뷰 창을 띄운다.
                $.post("http://localhost:9000/save-url",
                    {
                        "previewUrl": dataUrl,
                        "month": month
                    }
                ).done(openPreviewTap);
            }
        });
    }

    function openPreviewTap() {
        window.open('/preview', '인쇄 프리뷰', 'resizable=1,width=526,height=715');
    }
})();
