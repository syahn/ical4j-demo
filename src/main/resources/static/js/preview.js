
//option variables
var startOption = document.getElementById("start_month");
var endOption = document.getElementById("end_month");
var landscape = document.getElementById("rdo2_0");

var initialStartMonth = startOption.options[startOption.selectedIndex].value;

var startMonth, endMonth, orientation = 1;

var printMode = {
    "portrait": "width: 180px; height:260px;",
    "landscape": "width: 343px; height:260px;"
}

$(document).ready(function () {

    //select option 메인 페이지 달로 초기화
    $("#start_month").val($('#monthPreview').attr("value"));
    $("#end_month").val($('#monthPreview').attr("value"));
    initialStartMonth = startOption.options[startOption.selectedIndex].value;

    $("._close").click(function () {
        window.close();
    });

    $("#button-print").click(function () {

        optionApply();

        $.post("http://localhost:8080/convert",
            {
                "startMonth": startMonth,
                "endMonth": endMonth,
                "orientation": orientation
            }).done(function () {
            printPage("/tempPdf/month_result.pdf");
        });

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
    })
});

function optionApply() {

    //시작 월과 끝 월 파라미터 재설정
    startMonth = startOption.options[startOption.selectedIndex].value;
    endMonth = endOption.options[endOption.selectedIndex].value;

    //용지방향 재설정
    orientation = landscape.checked ? 1 : 0;

}

//convert url request
function save() {

    optionApply();

    var optionValue = {
        'startMonth': startMonth,
        'endMonth': endMonth,
        'orientation': orientation
    };

    $.ajax({
        url: "http://localhost:8080/convert",
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

        }
    });
}

//총 페이지 수 표시 및 프리뷰 이미지 첫달로 변경
function change() {
    var pageNum = document.getElementById("pageNum");

    // 총 페이지 수 계산
    optionApply();
    var numOfMonth = endMonth - startMonth + 1;

    if (startOption.selectedIndex != null) {
        pageNum.innerHTML = " 총 페이지 개수: " + numOfMonth;
        pageNum.style.display = "inline";
    }

    if (initialStartMonth !== startMonth) {
        //console.log("initial: " + initialStartMonth, "start: " + startMonth);
        initialStartMonth = startMonth;
        checkBox();
    }

}

//미리보기 세로방향, 가로방향 보여주기
function checkBox() {

    optionApply();

    var vertical = document.getElementById("rdo2_1").checked;

    if (vertical) {
        takeScreenShot(startMonth, "portrait");
    } else {
        takeScreenShot(startMonth, "landscape");
    }
}

function takeScreenShot(month, mode) {

    if (document.getElementById("hiddenFrame") != null) {
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
    hiddenFrame.style.visibility = "hidden";

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
    return "html/month_" + month + ".html";
}
