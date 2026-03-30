// 2026/03/30 S.Cyo Del Start 故障No.28対応 (跨页报错修复)
/*
async function doAction(operationName)
{
    var actionUrl = "";
    var obj = document.getElementById("ChohyologForm");
    switch (operationName)
    {
        case "pre":
            button_colorout(obj.prePageButton.style);
            obj.prePageButton.disabled = true;
            obj.nextPage.value = parseInt(obj.currentPage.value) - 1;
            actionUrl = "ZBB014S001PageController.do";
            allBtnDisabled(obj);
            // 2026/03/30 S.Cyo Add Start
            obj.no.value = "";
            // 2026/03/30 S.Cyo Add End
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;
        case "next":
            button_colorout(obj.nextPageButton.style);
            obj.nextPageButton.disabled = true;
            obj.nextPage.value = parseInt(obj.currentPage.value) + 1;
            actionUrl = "ZBB014S001PageController.do";
            allBtnDisabled(obj);
            // 2026/03/30 S.Cyo Add Start
            obj.no.value = "";
            // 2026/03/30 S.Cyo Add End
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;
        case "pgChg":
            button_colorout(obj.pgChgButton.style);
            obj.pgChgButton.disabled = true;
            obj.nextPage.value = obj.selectNextPage.value;
            actionUrl = "ZBB014S001PageController.do";
            allBtnDisabled(obj);
            // 2026/03/30 S.Cyo Add Start
            obj.no.value = "";
            // 2026/03/30 S.Cyo Add End
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;
        case "query":
            button_colorout(obj.queryButton.style);
            obj.queryButton.disabled = true;
            obj.menuNO.value = -1;
            obj.message.value = "検索中です。";
            actionUrl = "GetChohyologList.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;
//2024/04/24 DIS.Zhangjianting Add start 0.3.020.000:新WizLIFE 2 次開発
        case "update":
            button_colorout(obj.shobunk.style);
            var hakkoMeMo = document.getElementById("hakkoMeMo" + obj.no.value);
            obj.shobunk.disabled = true;
            obj.nextPage.value = obj.selectNextPage.value;
            obj.message.value = "更新中です。";
            actionUrl = "ZBB014S001UpdateController.do";
            hakkoMeMo.setAttribute("disabled", "disabled");
            obj.hakkoMeMo.value = hakkoMeMo.value;
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;
//2024/04/24 DIS.Zhangjianting Add end
        case "delete":
            var flg = await AsyncMessageCommon.asyncShowMessageConfirm("削除してもいいですか？", function(){ return true; }, function(){ return false; });
            if (flg)
            {
                button_colorout(obj.deleBtn.style);
                obj.deleBtn.disabled = true;
                obj.nextPage.value = obj.selectNextPage.value;
                obj.message.value = "削除中です。";
                actionUrl = "ZBB014S001DeleController.do";
                allBtnDisabled(obj);
                obj.action = "${pageContext.request.contextPath}/" + actionUrl;
                obj.submit();
            }
            break;
        case "hyoji":
            button_colorout(obj.imejiBtn.style);
            obj.imejiBtn.disabled = true;
            actionUrl = "ZBB014S001PrintController.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;
        default:
    }
}
*/
// 2026/03/30 S.Cyo Del End

// 2026/03/30 S.Cyo Add Start 故障No.28対応 (跨页报错修复)
async function doAction(operationName) {
    var actionUrl = "";
    var obj = document.getElementById("ChohyologForm") || document.getElementsByName("ChohyologForm")[0];
    
    if (!obj) {
        console.error("エラー: ChohyologForm が見つかりません。");
        return;
    }

    // 定义安全获取/设置值的方法，防止 null.value 报错
    function getSafeVal(name) {
        var el = obj.elements[name] || document.getElementsByName(name)[0] || document.getElementById(name);
        return el ? el.value : "";
    }
    function setSafeVal(name, val) {
        var el = obj.elements[name] || document.getElementsByName(name)[0] || document.getElementById(name);
        if (el) el.value = val;
    }

    switch (operationName) {
        case "pre":
            if(obj.prePageButton){
                button_colorout(obj.prePageButton.style);
                obj.prePageButton.disabled = true;
            }
            var curPage = parseInt(getSafeVal("currentPage")) || 1;
            setSafeVal("nextPage", curPage - 1);
            setSafeVal("no", ""); // 翻页时清空选中项
            actionUrl = "ZBB014S001PageController.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;

        case "next":
            if(obj.nextPageButton){
                button_colorout(obj.nextPageButton.style);
                obj.nextPageButton.disabled = true;
            }
            var curPage = parseInt(getSafeVal("currentPage")) || 1;
            setSafeVal("nextPage", curPage + 1);
            setSafeVal("no", ""); // 翻页时清空选中项
            actionUrl = "ZBB014S001PageController.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;

        case "pgChg":
            if(obj.pgChgButton){
                button_colorout(obj.pgChgButton.style);
                obj.pgChgButton.disabled = true;
            }
            setSafeVal("nextPage", getSafeVal("selectNextPage"));
            setSafeVal("no", ""); // 翻页时清空选中项
            actionUrl = "ZBB014S001PageController.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;

        case "query":
            if(obj.queryButton){
                button_colorout(obj.queryButton.style);
                obj.queryButton.disabled = true;
            }
            setSafeVal("menuNO", "-1");
            setSafeVal("no", "");
            setSafeVal("message", "検索中です。");
            actionUrl = "GetChohyologList.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;

        case "update":
            var currentNo = getSafeVal("no");
            if (currentNo === "" || currentNo === "-1") {
                alert("対象データを選択してください。");
                return;
            }
            if(obj.shobunk){
                button_colorout(obj.shobunk.style);
                obj.shobunk.disabled = true;
            }
            
            var hakkoMeMo = document.getElementById("hakkoMeMo" + currentNo);
            if (hakkoMeMo) {
                 hakkoMeMo.setAttribute("disabled", "disabled");
                 setSafeVal("hakkoMeMo", hakkoMeMo.value);
            }
            
            setSafeVal("nextPage", getSafeVal("selectNextPage"));
            setSafeVal("message", "更新中です。");
            actionUrl = "ZBB014S001UpdateController.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;

        case "delete":
            var flg = await AsyncMessageCommon.asyncShowMessageConfirm("削除してもいいですか？", function(){ return true; }, function(){ return false; });
            if (flg) {
                if(obj.deleBtn){
                    button_colorout(obj.deleBtn.style);
                    obj.deleBtn.disabled = true;
                }
                setSafeVal("nextPage", getSafeVal("selectNextPage"));
                setSafeVal("message", "削除中です。");
                actionUrl = "ZBB014S001DeleController.do";
                allBtnDisabled(obj);
                obj.action = "${pageContext.request.contextPath}/" + actionUrl;
                obj.submit();
            }
            break;

        case "hyoji":
            if(obj.imejiBtn){
                button_colorout(obj.imejiBtn.style);
                obj.imejiBtn.disabled = true;
            }
            actionUrl = "ZBB014S001PrintController.do";
            allBtnDisabled(obj);
            obj.action = "${pageContext.request.contextPath}/" + actionUrl;
            obj.submit();
            break;
    }
}
// 2026/03/30 S.Cyo Add End
