function clickOneRadio(objNo) {
    // 强制获取 Form，防止跨页后对象丢失
    var formObj = document.getElementById("ChohyologForm") || document.getElementsByName("ChohyologForm")[0];
    if (!formObj) return;

    formObj.no.value = objNo;
    
    var kindOfReportElem = document.getElementById("kindOfReport" + objNo);
    var kindOfReport = kindOfReportElem ? kindOfReportElem.innerHTML : "";

    // 增加 null 判断，防止跨页后找不到上一页的元素导致 JS 崩溃
    if (formObj.no.value != null && formObj.no.value != "") {
        var hakkoMeMo3 = document.getElementById("hakkoMeMo1" + formObj.no.value);
        var hakkoMeMo4 = document.getElementById("hakkoMeMo2" + formObj.no.value);
        if (hakkoMeMo4) {
            hakkoMeMo4.style.display = "none";
        }
        if (hakkoMeMo3) {
            hakkoMeMo3.style.display = "";
        }
    }

    // 强制解除处分更新按钮的禁用
    var shobunk = document.getElementById("shobunk") || document.getElementsByName("shobunk")[0];
    if (shobunk) {
        shobunk.removeAttribute("disabled");
        shobunk.disabled = false;
    }
}
