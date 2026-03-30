// 2026/03/30 S.Cyo Del Start 故障No.28対応 (ID取得漏れと幽霊更新)
/*
//2025/01/28 S.ZHANGZHIKAI Upd Start 0.3.020.000:ST故障No.28対応
}else if(functionId==<%= ZAB028S001Controller.FUNCTION_UPDATE %>){//处分更新
    var no = checkNo();
    var hakkoMeMo = document.getElementById("hakkoMeMo" + no);
    if (hakkoMeMo != null) {
        document.getElementById("ZAB028S001Form").hakkoMeMo.value = hakkoMeMo.value;
    }else{
        document.getElementById("ZAB028S001Form").hakkoMeMo.value = "";
    }
}
//2025/01/28 S.ZHANGZHIKAI Upd End
*/
// 2026/03/30 S.Cyo Del End

// 2026/03/30 S.Cyo Add Start 故障No.28対応 (正しいID参照と入力値チェック)
//2025/01/28 S.ZHANGZHIKAI Upd Start 0.3.020.000:ST故障No.28対応
}else if(functionId==<%= ZAB028S001Controller.FUNCTION_UPDATE %>){//处分更新
    var no = checkNo();
    // 修正: 表示が切り替わる入力欄の正しいIDである "hakkoMeMo2" を使用
    var hakkoMeMo = document.getElementById("hakkoMeMo2" + no);
    
    if (hakkoMeMo != null) {
        document.getElementById("ZAB028S001Form").hakkoMeMo.value = hakkoMeMo.value;
    }else{
        // 取得失敗時に空文字で更新してしまう「幽霊更新」を防止
        alert("システムエラー：画面から入力値(no=" + no + ")を取得できませんでした。");
        return false;
    }
}
//2025/01/28 S.ZHANGZHIKAI Upd End
// 2026/03/30 S.Cyo Add End
