// 2026/03/30 S.Cyo Del Start 故障No.28対応 (ページング時のNO取得バグ)
/*
function checkNo(){
    var rdoDels=window.document.ZAB028S001Form.rdoDel;
    for(var i=0;i<rdoDels.length;i++){
        if(rdoDels[i].checked==true){
            noForcheckde = i + 1;
            return i + 1;
        }
    }
}
*/
// 2026/03/30 S.Cyo Del End

// 2026/03/30 S.Cyo Add Start 故障No.28対応 (正確なNO値の取得に修正)
function checkNo(){
    var rdoDels = window.document.ZAB028S001Form.rdoDel;
    if (!rdoDels) return "";

    // データが1件しかない場合（配列ではない場合）の安全対応
    if (rdoDels.length === undefined) {
        if (rdoDels.checked) {
            noForcheckde = rdoDels.value;
            return rdoDels.value;
        }
    } else {
        // データが複数ある場合
        for(var i=0; i<rdoDels.length; i++){
            if(rdoDels[i].checked == true){
                // i + 1 ではなく、ラジオボタンにバインドされた本物の NO (vRow.NO) を返す
                noForcheckde = rdoDels[i].value;
                return rdoDels[i].value;
            }
        }
    }
    return "";
}
// 2026/03/30 S.Cyo Add End
