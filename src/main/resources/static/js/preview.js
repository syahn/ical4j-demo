//option variables
// (function () {

var startOption = document.getElementById("start_month");
var endOption = document.getElementById("end_month");
var landscape = document.getElementById("rdo2_0");

var initialStartMonth;
var startMonth, endMonth, orientation;
var printMode = {
    "portrait": "width: 180px; height:260px;",
    "landscape": "width: 343px; height:260px;"
};

$(document).ready(function () {

    //select option 메인 페이지 달로 초기화

    initiatePeriod();
    changePreviewImage();

    $("._close").click(closeWindow);
    $("#button-print").click(requestPrint);
    $("#button-save").click(requestSave);
    $("#start_month").on("change", changePreviewImage);
    $("#end_month").on("change", changePeriod);
    $("._portrait, ._landscape").click(changeOrientation);

});

function closeWindow() {
    window.close();
}

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

    if (startOption.selectedIndex != null) {
        pageNum.innerHTML = " 총 페이지 개수: " + numOfMonth;
        pageNum.style.display = "inline";
    }
}

function refreshOptions() {
    //시작 월과 끝 월 파라미터 재설정
    startMonth = startOption.options[startOption.selectedIndex].value;
    endMonth = endOption.options[endOption.selectedIndex].value;

    //용지방향 재설정
    orientation = landscape.checked ? 1 : 0;
}

//미리보기 세로방향, 가로방향 보여주기
function changeOrientation() {
    enableLoader();
    refreshOptions();

    var vertical = document.getElementById("rdo2_1").checked;

    if (vertical) {
        takeScreenShot(startMonth, "portrait");
    } else {
        takeScreenShot(startMonth, "landscape");
    }

}

function enableLoader() {
    document.getElementById('loader').style.display = 'block';
    document.getElementById('previewImage').style.display = 'none';
}

function takeScreenShot(startMonth, mode) {

    $.post("/make-preview", {
        startMonth: startMonth,
        endMonth: startMonth,
        currentYear: 2017
    }).done(function () {

        if (document.getElementById("hiddenFrame") !== null) {
            var elem = document.getElementById("hiddenFrame");
            elem.parentNode.removeChild(elem);
        }

        makeDummyWindow(startMonth);//새로 생성된 html파일 불러와 iframe 만듬

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
        // $("#hiddenFrame").css("visibility", "hidden");
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

function requestSave() {

    document.getElementById("saveText").style.display = "none";
    document.getElementById("save-loader").style.display = "block";
    refreshOptions();

    var optionValue = {
        startMonth: startMonth,
        endMonth: endMonth,
        currentYear: 2017, // 임시
        orientation: orientation
    };

    $.post("http://localhost:9000/convert", optionValue).done(function () {
        var dataURI = '/tempPdf/month_result.pdf';
        var fileName = 'Calendar.pdf';

        save(dataURI, fileName);

        document.getElementById("save-loader").style.display = "none";
        document.getElementById("saveText").style.display = "block";
    });

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
    document.getElementById("printText").style.display = "none";
    document.getElementById("print-loader").style.display = "block";

    $.post("http://localhost:9000/convert",
        {
            startMonth: startMonth,
            endMonth: endMonth,
            currentYear: 2017, // 임시
            orientation: orientation
        }).done(function () {

        printPDF("/tempPdf/month_result.pdf");
        document.getElementById("printText").style.display = "block";
        document.getElementById("print-loader").style.display = "none";
    });

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
    console.log(oHiddFrame);
}


function printPDF(url) {

    var agent = navigator.userAgent.toLowerCase(); // ie아닌경우 agent 인식 위함

    if ((navigator.appName == 'Netscape' && navigator.userAgent.search('Trident') != -1) || (agent.indexOf("msie") != -1)) {

        //방법 1 - 새창에 pdf파일을 표시하여 print 실행 - 로드는 되지만 실행 안됨 / 크롬은 정상동작
        // setTimeout(function () {
        //     var W = window.open(url);
        //     W.print();
        // },3000);

        //방법 2 - iframe에 pdf파일을 로드시킨후 print 실행 - 로드 되고 프린트 실행시
        //$("#hiddenFrame").attr("src","/tempPdf/printable.pdf");
        // setTimeout(function () {
        //     $("#hiddenFrame").get(0).focus();
        //     // $("#hiddenFrame").get(0).contentWindow.document.execCommand('print', false, null);
        // },3000);

        //방법 3 - object에 pdf파일 임베딩
        // if($("#obj").length!=0){
        //     $("#pdfDocument").empty();
        // }

        // var newElement = '<object id="obj" '+
        //     'width="300" height="400" type="application/pdf"' +
        //     'data="' + '/tempPdf/printable.pdf' + '?#view=Fit&scrollbar=0&toolbar=0&navpanes=0">' +
        //     '</object>';

        // setTimeout(function () {
        //     var el = document.getElementById("obj");
        //     el.focus();
        //     el.print();
        // }, 1000);


        //방법 4 - pdf파일에 auto printable js 삽입
        var oHiddFrame = document.createElement("iframe");
        oHiddFrame.src="http://localhost:9000/tempPdf/printable.pdf"
        $("#pdfDocument").append(oHiddFrame);

    }
    else{
        printPage(url);
    }

}

