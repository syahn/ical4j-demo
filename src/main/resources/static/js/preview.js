//option variables
(function () {

    var startOption = document.getElementById("start_month");
    var endOption = document.getElementById("end_month");
    var landscape = document.getElementById("rdo2_0");


    var initialStartMonth;
    var startMonth, endMonth, orientation;
    var printMode = {
        "portrait": "width: 180px; height:260px;",
        "landscape": "width: 343px; height:260px;"
    };

    var fileID;

    $(document).ready(function () {

        //select option 메인 페이지 달로 초기화

        fileID = $("#content").attr("value");

        initiatePeriod();
        changePreviewImage();

        $("#button-print").click(requestPrint);
        $("#button-save").click(requestSave);
        $("#start_month").on("change", changePreviewImage);
        $("#end_month").on("change", changePeriod);
        $("._portrait, ._landscape").click(changeOrientation);

    });

    function initiatePeriod() {
        $("#start_month").val($('#monthPreview').attr("value"));
        $("#end_month").val($('#monthPreview').attr("value"));

        initialStartMonth = startOption.options[startOption.selectedIndex].value;
    }

    function changePreviewImage() {
        changePeriod();
        changeOrientation();
    }

    function changePeriod() {
        var pageNum = document.getElementById("pageNum");

        refreshOptions();

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


        if (startMonth > endMonth) {
            $("#end_month").val(startMonth);
            endMonth = startMonth;
        }

        //용지방향 재설정
        orientation = landscape.checked ? 1 : 0;
    }


    //미리보기 세로방향, 가로방향 보여주기
    function changeOrientation() {
        enablePreviewLoader();
        refreshOptions();

        var vertical = document.getElementById("rdo2_1").checked;

        if (vertical) {
            takeScreenShot(startMonth, "portrait");
        } else {
            takeScreenShot(startMonth, "landscape");
        }
    }

    function enablePreviewLoader() {
        document.getElementById('loader').style.display = 'block';
        document.getElementById('previewImage').style.display = 'none';
    }

    function takeScreenShot(startMonth, mode) {


        $.post("/make-preview", {
            startMonth: startMonth,
            endMonth: startMonth,
            fileID: fileID,
            currentYear: 2017
        }).done(function () {

            console.log(document.cookie);

            if (document.getElementById("hiddenFrame") !== null) {
                var elem = document.getElementById("hiddenFrame");
                elem.parentNode.removeChild(elem);

            }


            makeDummyWindow(startMonth.toString() + fileID);//새로 생성된 html파일 불러와 iframe 만듬

            html2canvas(document.getElementById("hiddenFrame"), {
                onrendered: function (canvas) {
                    //이미지
                    var dataUrl = canvas.toDataURL();
                    $("#previewImage").attr({
                        "src": dataUrl,
                        "style": mode === "landscape" ? printMode.landscape : printMode.portrait
                    });
                    $("#loader").css("display", "none");
                }
            });
            $("#hiddenFrame").css("visibility", "hidden");
        });
    }


    function makeDummyWindow(month) {
        var hiddenFrame = document.createElement("iframe");
        hiddenFrame.id = "hiddenFrame";
        hiddenFrame.width = "1000";
        hiddenFrame.height = "1000";
        hiddenFrame.frameBorder = "0";
        hiddenFrame.src = generateNewUrl(month);
        document.body.appendChild(hiddenFrame);
    }


    function generateNewUrl(path) {
        return "/html/month" + path + ".html";
    }


    function requestSave() {


        refreshOptions();
        enableSaveLoader();


        var optionValue = {
            startMonth: startMonth,
            endMonth: endMonth,
            currentYear: 2017, // 임시
            orientation: orientation,
            fileID: fileID,
            type: "save"
        };

        $.post("http://localhost:9000/convert", optionValue).done(function () {
            var dataURI = '/tempPdf/month_result' + fileID + '.pdf';
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
                fileID: fileID,
                type: "print"
            }).done(function () {

            $("#hiddenFrame").attr("src", "/tempPdf/month_result" + fileID + ".pdf");

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