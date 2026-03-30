// 2026/03/30 S.Cyo Del Start 故障No.28対応 (跨页报错修复)
/*
function clickOneRadio(objNo)
{
    var formObj = document.getElementById("ChohyologForm");
    //2024/04/24 DIS.Zhangjianting Update start 0.3.020.000:新WizLIFE 2 次開発
    //formObj.no.value = objNo;
    var kindOfReport = "kindOfReport" + objNo;
    var kindOfReport = document.getElementById(kindOfReport).innerHTML;
    
    if (formObj.no.value != objNo){
    // 2025/01/30 S.Raw Del 0.3.020.000:ST故障No.28対応
    // if (kindOfReport == "納税証明書" || kindOfReport == "営業証明書"){
        var hakkoMeMo1 = document.getElementById("hakkoMeMo1" + objNo);
        var hakkoMeMo2 = document.getElementById("hakkoMeMo2" + objNo);
        hakkoMeMo1.style.display = "none";
        hakkoMeMo2.style.display = "";
        if (formObj.no.value != null && formObj.no.value != ""){
            var hakkoMeMo3 = document.getElementById("hakkoMeMo1" + formObj.no.value);
            var hakkoMeMo4 = document.getElementById("hakkoMeMo2" + formObj.no.value);
            // 2026/03/30 S.Cyo Upd Start
            //hakkoMeMo4.style.display = "none";
            //hakkoMeMo3.style.display = "";
            if (hakkoMeMo4 != null){
                hakkoMeMo4.style.display = "none";
            }
            if (hakkoMeMo3 != null){
                hakkoMeMo3.style.display = "";
            }
            // 2026/03/30 S.Cyo Upd End
        }
        var shobunk = document.getElementById("shobunk");
        shobunk.removeAttribute("disabled");
    // 2025/01/30 S.Raw Del Start 0.3.020.000:ST故障No.28対応
    // } else {
    //     var hakkoMeMo1 = document.getElementById("hakkoMeMo1" + formObj.no.value);
    //     var hakkoMeMo2 = document.getElementById("hakkoMeMo2" + formObj.no.value);
    //     hakkoMeMo2.style.display = "none";
    //     hakkoMeMo1.style.display = "";
    //     var shobunk = document.getElementById("shobunk");
    //     shobunk.setAttribute("disabled","true");
    // }
    // 2025/01/30 S.Raw Del End
        formObj.no.value = objNo;
    }
}
*/
// 2026/03/30 S.Cyo Del End

// 2026/03/30 S.Cyo Add Start 故障No.28対応 (跨页报错修复)
function clickOneRadio(objNo) {
    var formObj = document.getElementById("ChohyologForm") || document.getElementsByName("ChohyologForm")[0];
    if (!formObj) return;

    var kindOfReportElem = document.getElementById("kindOfReport" + objNo);
    var kindOfReport = kindOfReportElem ? kindOfReportElem.innerHTML : "";

    if (formObj.no.value != objNo) {
        var hakkoMeMo1 = document.getElementById("hakkoMeMo1" + objNo);
        var hakkoMeMo2 = document.getElementById("hakkoMeMo2" + objNo);
        if (hakkoMeMo1) hakkoMeMo1.style.display = "none";
        if (hakkoMeMo2) hakkoMeMo2.style.display = "";

        if (formObj.no.value != null && formObj.no.value != "") {
            var hakkoMeMo3 = document.getElementById("hakkoMeMo1" + formObj.no.value);
            var hakkoMeMo4 = document.getElementById("hakkoMeMo2" + formObj.no.value);
            if (hakkoMeMo4) hakkoMeMo4.style.display = "none";
            if (hakkoMeMo3) hakkoMeMo3.style.display = "";
        }
        
        var shobunk = document.getElementById("shobunk") || document.getElementsByName("shobunk")[0];
        if (shobunk) {
            shobunk.removeAttribute("disabled");
            shobunk.disabled = false;
        }
        formObj.no.value = objNo;
    }
}
// 2026/03/30 S.Cyo Add End
