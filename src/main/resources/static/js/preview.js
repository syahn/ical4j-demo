//option variables
(function () {

    var startOption = document.getElementById("start_month");
    var endOption = document.getElementById("end_month");
    var landscape = document.getElementById("rdo2_0");

    var initialStartMonth = startOption.options[startOption.selectedIndex].value;
    var startMonth, endMonth, orientation = 1;
    var printMode = {
        "portrait": "width: 180px; height:260px;",
        "landscape": "width: 343px; height:260px;"
    };

    $(document).ready(function () {

        //select option 메인 페이지 달로 초기화
        $("#start_month").val($('#monthPreview').attr("value"));
        $("#end_month").val($('#monthPreview').attr("value"));

        initialStartMonth = startOption.options[startOption.selectedIndex].value;

        $("._close").click(function () {
            window.close();
        });

        $("#button-print").click(function () {

            refreshOptions();

            $.post("/print-change-range", {
                start: startMonth,
                end: endMonth
            }).done(function () {
                $.post("http://localhost:9000/convert",
                    {
                        "startMonth": startMonth,
                        "endMonth": endMonth,
                        "orientation": orientation
                    }).done(function () {
                    printPage("/tempPdf/month_result.pdf");
                    document.getElementById("loader").style.display = "none";
                });
            });
        });

        $("#button-save").click(requestSave);
        $("#start_month").on("change", changePreviewImage);
        $("#end_month").on("change", changePeriod);
        $("._portrait, ._landscape").click(changeOrientation);
    });

    function refreshOptions() {

        document.getElementById("loader").style.display = "block";
        //시작 월과 끝 월 파라미터 재설정
        startMonth = startOption.options[startOption.selectedIndex].value;
        endMonth = endOption.options[endOption.selectedIndex].value;

        //용지방향 재설정
        orientation = landscape.checked ? 1 : 0;
    }

    //convert url request
    function requestSave() {

        refreshOptions();
        $.post("/print-change-range", {
            start: startMonth,
            end: endMonth
        }).done(function () {
            var optionValue = {
                'startMonth': startMonth,
                'endMonth': endMonth,
                'orientation': orientation
            };

            $.ajax({
                url: "http://localhost:9000/convert",
                type: "POST",
                data: optionValue,
                success: function () {

                    // 서버 임시 pdf 변환 파일 불러온 후 save box 팝업
                    var dataURI = '/tempPdf/month_result.pdf';

                    var fileName = 'Calendar';

                    var link = document.getElementById("saveLink");

                    link.setAttribute("href", dataURI);
                    link.setAttribute("download", fileName);
                    link.click();

                    document.getElementById("loader").style.display = "none";
                }
            });
        });
    }

    //총 페이지 수 표시 및 프리뷰 이미지 첫달로 변경
    function changePreviewImage() {
        changePeriod();

        if (initialStartMonth !== startMonth) {
            //console.log("initial: " + initialStartMonth, "start: " + startMonth);
            initialStartMonth = startMonth;
            changeOrientation();
        }
    }

    function changePeriod() {
        var pageNum = document.getElementById("pageNum");

        refreshOptions();

        notifyPeriod();

        // 총 페이지 수 계산
        var numOfMonth = endMonth - startMonth + 1;

        if (startOption.selectedIndex != null) {
            pageNum.innerHTML = " 총 페이지 개수: " + numOfMonth;
            pageNum.style.display = "inline";
        }
    }

    function notifyPeriod() {
        $.post("/print-change-range", {
            start: startMonth,
            end: endMonth
        });
    }

    //미리보기 세로방향, 가로방향 보여주기
    function changeOrientation() {

        refreshOptions();

        var vertical = document.getElementById("rdo2_1").checked;

        if (vertical) {
            takeScreenShot(startMonth, "portrait");
        } else {
            takeScreenShot(startMonth, "landscape");
        }
    }

    // function renderPortraitView() {
    //     takeScreenShot(startMonth, "portrait");
    // }
    //
    // function renderLandscapeView() {
    //     takeScreenShot(startMonth, "landscape");
    // }

    function takeScreenShot(month, mode) {
        $.post("/print-change-range", {
            start: month,
            end: month
        }).done(function () {
            $.post("/make-preview").done(function () {
                if (document.getElementById("hiddenFrame") !== null) {
                    var elem = document.getElementById("hiddenFrame");
                    elem.parentNode.removeChild(elem);
                }

                makeDummyWindow(month);

                html2canvas(document.getElementById("hiddenFrame"), {
                    onrendered: function (canvas) {
                        //이미지
                        var dataUrl = canvas.toDataURL();
                        $("#previewImage").attr({
                            "src": dataUrl,
                            "style": mode === "landscape" ? printMode.landscape : printMode.portrait
                        });
                    }
                });
                document.getElementById("loader").style.display = "none";
            });
        });
    }

    function makeDummyWindow(month) {

        var hiddenFrame = document.createElement("iframe");
        hiddenFrame.setAttribute("id", "hiddenFrame");
        hiddenFrame.setAttribute("width", "1000");
        hiddenFrame.setAttribute("height", "1000");
        hiddenFrame.setAttribute("frameBorder", "0");
        hiddenFrame.style.marginTop = "100px";
        document.body.appendChild(hiddenFrame);

        $("#hiddenFrame").attr("src", generateNewUrl(month));
    }

    function generateNewUrl(month) {
        return "/html/month" + month + ".html";
    }

    function closePrint() {
        document.body.removeChild(this.__container__);
    }

    function setPrint() {
        this.contentWindow.__container__ = this;
        this.contentWindow.onbeforeunload = closePrint;
        this.contentWindow.onafterprint = closePrint;
        this.contentWindow.focus(); // Required for IE
        this.contentWindow.print();
    }

    function printPage(sURL) {
        var oHiddFrame = document.createElement("iframe");
        oHiddFrame.onload = setPrint;
        oHiddFrame.style.visibility = "hidden";
        oHiddFrame.style.position = "fixed";
        oHiddFrame.style.right = "0";
        oHiddFrame.style.bottom = "0";
        oHiddFrame.src = sURL;
        document.body.appendChild(oHiddFrame);
    }

})();
