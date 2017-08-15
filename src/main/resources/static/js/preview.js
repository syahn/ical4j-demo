//option variables
(function () {
    var startOption = document.getElementById("start_month");
    var endOption = document.getElementById("end_month");
    var landscape = document.getElementById("rdo2_0");
    var fontSizeOpt = document.getElementById("font_size_select");
    var vertical = document.getElementById("rdo2_1").checked ? "portrait" : "landscape";
    var templateType = $(":input:radio[name=print_item]:checked").val();;
    var initialStartMonth;
    var startMonth, endMonth, orientation;
    var fontSize = fontSizeOpt.options[fontSizeOpt.selectedIndex].value;
    var printMode = {
        "portrait": "width: 180px; height:260px;",
        "landscape": "width: 343px; height:260px;"
    };
    var fileID, userID;

    $(document).ready(function () {

        //select option 메인 페이지 달로 초기화
        fileID = $("#content").attr("value");
        userID = $("#header").attr("value");

        initiatePeriod();
        changePreviewImage();

        $("#button-print").click(requestPrint);
        $("#button-save").click(requestSave);
        $("#start_month").on("change", changePreviewImage);
        $("#end_month").on("change", changePeriod);
        $("._print_item").on("change", changePreviewImage);
        $("._font_size_select").on("change", changePreviewImage);
        $("._portrait, ._landscape").click(changePreviewImage);
    });

    function initiatePeriod() {
        $("#start_month").val($('#monthPreview').attr("value"));
        $("#end_month").val($('#monthPreview').attr("value"));

        initialStartMonth = startOption.options[startOption.selectedIndex].value;
    }

    function changePreviewImage() {
        enablePreviewLoader();
        refreshOptions();
        takeScreenShot();
    }

    function changePeriod() {
        var pageNum = document.getElementById("pageNum");
        startMonth = startOption.options[startOption.selectedIndex].value;
        endMonth = endOption.options[endOption.selectedIndex].value;

        // 총 페이지 수 계산
        var numOfMonth = endMonth - startMonth + 1;

        if (startOption.selectedIndex !== null) {
            pageNum.innerHTML = " 총 페이지 개수: " + numOfMonth;
            pageNum.style.display = "inline";
        }
    }

    function refreshOptions() {
        //시작 월과 끝 월 파라미터 재설정
        startMonth = startOption.options[startOption.selectedIndex].value;
        endMonth = endOption.options[endOption.selectedIndex].value;
        vertical = document.getElementById("rdo2_1").checked ? "portrait" : "landscape";
        templateType = $(":input:radio[name=print_item]:checked").val();
        fontSize =fontSizeOpt.options[fontSizeOpt.selectedIndex].value;

        if (startMonth > endMonth) {
            $("#end_month").val(startMonth);
            endMonth = startMonth;
        }

        //용지방향 재설정
        orientation = landscape.checked ? 1 : 0;
    }

    function enablePreviewLoader() {
        document.getElementById('loader').style.display = 'block';
        document.getElementById('previewImage').style.display = 'none';
    }

    function takeScreenShot() {
        $.post("/make-preview", {
            startMonth: startMonth,
            endMonth: startMonth,
            templateType: templateType,
            fontSize: fontSize,
            userID: userID,
            fileID: fileID,
            currentYear: 2017
        }).done(function () {

            if (document.getElementById("hiddenFrame") !== null) {
                var elem = document.getElementById("hiddenFrame");
                elem.parentNode.removeChild(elem);
            }
            makeDummyWindow(userID, fileID, startMonth.toString());//새로 생성된 html파일 불러와 iframe 만듬

            $.get("/html/" + userID + "/" + startMonth + "/" + fileID + "/html-request")
                .done(function (e) {
                    // var file = window.URL.createObjectURL(e);
                    var frame = document.getElementById('hiddenFrame'),
                        framedoc = frame.contentDocument || frame.contentWindow.document;

                    framedoc.body.innerHTML = e;

                    convertHtmlToCanvas(framedoc);
                });
        });
    }

    function convertHtmlToCanvas(framedoc) {
        html2canvas(framedoc.body, {
            onrendered: function (canvas) {
                //이미지
                var dataUrl = canvas.toDataURL();
                $("#previewImage").attr({
                    "src": dataUrl,
                    "style": vertical === "landscape" ? printMode.landscape : printMode.portrait
                });
                $("#loader").css("display", "none");
            }
        });
        $("#hiddenFrame").css("visibility", "hidden");
    }

    function makeDummyWindow() {
        var hiddenFrame = document.createElement("iframe");
        hiddenFrame.id = "hiddenFrame";
        hiddenFrame.width = "1000";
        hiddenFrame.height = "1000";
        hiddenFrame.frameBorder = "0";
        document.body.appendChild(hiddenFrame);
    }

    function requestSave() {

        refreshOptions();
        enableSaveLoader();

        $.post("http://localhost:9000/convert", {
            startMonth: startMonth,
            endMonth: endMonth,
            currentYear: 2017, // 임시
            orientation: orientation,
            templateType: templateType,
            fontSize: fontSize,
            userID: userID,
            fileID: fileID,
            type: "save"
        }).done(function () {
            var dataURI = "/tempPdf/" + userID + "/" + fileID + "/pdf-request";
            var fileName = 'Calendar.pdf';
            save(dataURI, fileName);
            setTimeout(disableSaveLoader, 500);
        });
    }

    function enableSaveLoader() {
        document.getElementById("saveText").style.display = "none";
        document.getElementById("save-loader").style.display = "block";
    }

    function disableSaveLoader() {
        document.getElementById("save-loader").style.display = "none";
        document.getElementById("saveText").style.display = "block";
    }

    function save(fileURL, fileName) {

        var agent = navigator.userAgent.toLowerCase(); // ie아닌경우 agent 인식 위함

        //for IE
        if ((navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1)) {

            //for IE<=10
            if (agent.indexOf("msie") != -1) {
                var _window = window.open(fileURL, '_blank');
                _window.document.close();
                _window.document.execCommand('SaveAs', true, fileName || fileURL);
                _window.close();
            }
            //for IE>10
            else {
                var xhr = new XMLHttpRequest();
                xhr.open('GET', fileURL, true);
                xhr.responseType = 'blob';
                xhr.onload = function (e) {
                    if (this.status == 200) {
                        var blobObject = new Blob([this.response], {type: 'application/pdf'});
                        window.navigator.msSaveOrOpenBlob(blobObject, fileName);
                    }
                };
                xhr.send();
            }
        }
        //for non-IE
        else {
            //alert("인터넷 익스플로러 브라우저가 아닙니다.");
            var link = document.createElement('a');
            link.setAttribute("href", fileURL);
            link.setAttribute("download", fileName);
            link.click();
        }
    }

    function requestPrint() {

        refreshOptions();
        enablePrintLoader();

        $.post("http://localhost:9000/convert",
            {
                startMonth: startMonth,
                endMonth: endMonth,
                currentYear: 2017, // 임시
                orientation: orientation,
                templateType: templateType,
                fontSize: fontSize,
                userID: userID,
                fileID: fileID,
                type: "print"
            }).done(function () {
                $("#hiddenFrame").attr("src", "/tempPdf/" + userID + "/" + fileID + "/pdf-request");
                setTimeout(disablePrintLoader, 1000);
            });
    }

    function enablePrintLoader() {
        document.getElementById("printText").style.display = "none";
        document.getElementById("print-loader").style.display = "block";
    }

    function disablePrintLoader() {
        document.getElementById("print-loader").style.display = "none";
        document.getElementById("printText").style.display = "block";
    }
})();