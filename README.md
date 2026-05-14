没问题！既然我们要把这个 Bug 彻底修好，那就需要落实到具体的代码行和具体的改法上。
为了让您（或者负责开发的同事）能够直接参考并进行代码修改，我把针对前端 (ZAB001S007.jsp) 和后端 (ZAB001S007_FsokujiDaoImpl.java) 的详细改修方案给您梳理成“傻瓜式”的替换指南。
### 详细步骤 1：后端 Java 逻辑拆分与移动 (ZAB001S007_FsokujiDaoImpl.java)
这个文件里的问题在于：**“防重校验”和“数据入库”被绑在一起，而且执行得太早了。** 我们需要把它们拆开。
**动作 A：修改原位置代码（保留校验，删除入库）**
 * **定位**：参考 image_70.png，找到大约第 550 行（2024/05/07 DIS.Zhangjiurui Add start 的位置）。
 * **修改方式**：将原来的 if ("1".equals(searchInfo.getHenreiJohoToroku())) { ... } 整个代码块，**替换为以下只做拦截校验的代码**：
```java
// 2024/05/07 DIS.Zhangjiurui Add start (改修：仅保留排他校验)
// 返戻情報登録有無データ存在チェックを実施する。
if ("1".equals(searchInfo.getHenreiJohoToroku())) {
    Map<String, Object> checkParamMap = new HashMap<String, Object>();
    checkParamMap.put("gyoumuCd", ZABConstants.CS_GYOMUCODE); // 業務コード = ZAB
    checkParamMap.put("gyoumuSyosaiCd", ZABConstants.CS_GYOMUSUBCODE); // 業務詳細コード = 0
    checkParamMap.put("chohyoCd", searchInfo.getSelectId()); // 帳票コード
    checkParamMap.put("nendobun", searchInfo.getNendobun()); // 年度分
    checkParamMap.put("choteiNendo", searchInfo.getChotei_nendo()); // 調定年度
    checkParamMap.put("tsuchiNo", searchInfo.getTsuchi_no()); // 通知書番号
    checkParamMap.put("hassoubi", searchInfo.getSendDate_num()); // 発送日
    
    int count = zab0010Repository.selectZRATKOUJI_INF_065(checkParamMap);
    // 件数チェックし0件の時 -> 不是0件说明已经注册过，直接拦截！
    if (count != 0) {
        returnVector.add(StringUtils.EMPTY); // 即時帳票発行履歴を登録しない設定
        returnVector.add("1"); // 返回1触发Controller层的业务报错
        return returnVector;
    }
}
// 2024/05/07 DIS.Zhangjiurui Add end

```
**动作 B：在打印完成后追加“数据入库”逻辑**
 * **定位**：参考 image_82.png，找到大约第 712 行的结尾。这里是 PL/SQL (execOnlinePrint) 执行成功，且各种常规日志记录完毕，马上要 return 的地方。
 * **修改方式**：在 returnVector.add(reportUri); （大约 713 行）的**正上方**，插入我们刚才删掉的数据入库逻辑：
```java
        // ====== 【本次改修追加 start】：在PL/SQL生成台账数据后，再执行返戻信息的入库 ======
        if ("1".equals(searchInfo.getHenreiJohoToroku())) {
            // 普徴通知書（ブッキング様式対応版）の情報を取得する
            List<Map<String, Object>> listMap = getZABWFUCHBOOK(searchInfo);
            if (listMap != null && !listMap.isEmpty()) {
                for (int i = 0; i < listMap.size(); i++) {
                    // 登録データ編集
                    Map<String, Object> insertMap = getZratkoujiInf(listMap.get(i), searchInfo.getSendDate_num());
                    // 公示送達情報を登録する
                    zab0010Repository.insertZRATKOUJI_INF_067(insertMap);
                }
            }
        }
        // ====== 【本次改修追加 end】 ======

        returnVector.add(reportUri); // 这是原本就有的代码
        returnVector.add("0");       // 这是原本就有的代码
        return returnVector;         // 这是原本就有的代码
    }

```
### 详细步骤 2：前端 JSP 页面放开限制 (ZAB001S007.jsp)
前端的问题在于：代码写死了只有当报表 ID 为 410 (普征纳税通知书) 时，复选框才能用。根据 QA 的要求，我们需要把 411 (变更通知书) 等相关报表也一并放开。
**动作 A：修改 JavaScript 交互逻辑 (OnChange 触发)**
 * **定位**：参考 image_11.png，找到 doPrintChange(obj) 函数，大约在第 268 行到 274 行之间。
 * **修改方式**：原本只有 if (i == 410) 里有激活复选框的代码。我们需要在 else if (i == 411 || i == 413 || i == 414 || i == 412) 这个分支里，也加上激活代码：
```javascript
    // ...
    if (i == 410) {
        document.ZAB001S007Form.st_01.disabled = false;
        document.ZAB001S007Form.ed_01.disabled = false;
        document.ZAB001S007Form.sel_check.disabled = false; // 410 原本就有
        document.ZAB001S007Form.st_01Hidden.value = document.ZAB001S007Form.st_01.value;
        // ...
    } else if (i == 411 || i == 413 || i == 414 || i == 412) {
        document.ZAB001S007Form.st_11_num.disabled = false;
        document.ZAB001S007Form.st_12_num.disabled = false;
        // ...
        // ====== 【本次改修追加】：让变更通知书等也能点击复选框 ======
        document.ZAB001S007Form.sel_check.disabled = false; 
    }

```
**动作 B：修改 HTML 初始渲染逻辑 (页面初次加载)**
 * **定位**：参考 image_22.png 和 image_23.png，找到大约第 1476 行到 1511 行。这里用 JSTL 标签 <c:if test="..."> 控制了页面刚打开时的状态。
 * **修改方式**：将硬编码的 410 条件扩大，包含进 QA 要求的 411、412 等（具体包含哪些ID，请根据业务需求确认，这里假设 410 到 414 都需要放开）。
找到这段代码：
```jsp
<c:if test="${ZAB001S007_VIEW.selectId != '410'}">
    <!-- 这里面渲染的是 disabled="true" 的输入框和复选框 -->
</c:if>
<c:if test="${ZAB001S007_VIEW.selectId == '410'}">
    <!-- 这里面渲染的是正常可用的输入框和复选框 -->
</c:if>

```
**修改为：**
```jsp
<%-- 将条件改为排除 410, 411, 412, 413, 414 --%>
<c:if test="${ZAB001S007_VIEW.selectId != '410' && ZAB001S007_VIEW.selectId != '411' && ZAB001S007_VIEW.selectId != '412' && ZAB001S007_VIEW.selectId != '413' && ZAB001S007_VIEW.selectId != '414'}">
    <!-- 这里面渲染的是 disabled="true" 的代码保持不变 -->
</c:if>

<%-- 将条件改为包含 410, 411, 412, 413, 414 --%>
<c:if test="${ZAB001S007_VIEW.selectId == '410' || ZAB001S007_VIEW.selectId == '411' || ZAB001S007_VIEW.selectId == '412' || ZAB001S007_VIEW.selectId == '413' || ZAB001S007_VIEW.selectId == '414'}">
    <!-- 这里面渲染的是正常的代码保持不变 -->
</c:if>

```
**总结一下这次改修的灵魂：**
 * **前端**：不再“偏心” 410，把 411 等报表的复选框开关也一并打开。
 * **后端**：查重防抖留在打印前，落库 Insert 挪到打印后。
按这个详细方案改完，QA #28598 这个工单就可以完美关单了！
