根据您提供的最新截图（image_63.png 至 image_66.png），我已经为您整理好“步骤1（前端解绑）”和“步骤2（后端解绑）”的具体改修代码。
您可以直接参考以下代码对比进行修改。
### 🛠️ 步骤 1：前端解除复选框置灰限制（对应 image_63.png）
**修改文件**：ZAB002S004.jsp
**修改位置**：约第 517 行 if (i == 509) 的逻辑处。
**改修说明**：目前代码把 henkoOption（变更选项）和 henreiCheckValue（返还信息复选框）强行绑在一起，且只对 509 开放。我们需要将它们**拆开独立控制**。
**具体代码**：
```javascript
// ====== 【修改前】 (image_63.png 第517行起) ======
if (i == 509) {
    if (document.ZAB002S004Form.henkoOption) {
        document.ZAB002S004Form.henkoOption.disabled = false;
    }
    if (document.ZAB002S004Form.henreiCheckValue) {
        document.ZAB002S004Form.henreiCheckValue.disabled = false;
    }
} else {
    if (document.ZAB002S004Form.henkoOption) {
        document.ZAB002S004Form.henkoOption.disabled = true;
    }
    if (document.ZAB002S004Form.henreiCheckValue) {
        $("#henreiCheckValue").attr("checked", false);
        document.ZAB002S004Form.henreiCheckValue.disabled = true;
    }
}

// ====== 【修改后】 ======
// 1. 保留原本专门针对 509 的 henkoOption 逻辑
if (i == 509) {
    if (document.ZAB002S004Form.henkoOption) {
        document.ZAB002S004Form.henkoOption.disabled = false;
    }
} else {
    if (document.ZAB002S004Form.henkoOption) {
        document.ZAB002S004Form.henkoOption.disabled = true;
    }
}

// 2. 将 henreiCheckValue 的逻辑独立出来，放开特征的多个账票
// ID含义：508(特徵納税-個人), 509(特徵変更-個人), 514(特徵納税-事業所), 515(特徵変更-事業所)
// （注：如果在另一个对话里已经加入了普徴的ID，请把它们合并到这个数组里）
var allowedHenreiIds = [508, 509, 514, 515]; 

if (allowedHenreiIds.includes(parseInt(i))) {
    if (document.ZAB002S004Form.henreiCheckValue) {
        document.ZAB002S004Form.henreiCheckValue.disabled = false;
    }
} else {
    if (document.ZAB002S004Form.henreiCheckValue) {
        $("#henreiCheckValue").attr("checked", false); // 不在允许列表内的，取消勾选
        document.ZAB002S004Form.henreiCheckValue.disabled = true; // 并且置灰
    }
}

```
### 🛠️ 步骤 2：后端动态生成账票标题与处理个人姓名（对应 image_65.png）
**修改文件**：ZAB002S004_TsokujiDaoImpl.java
**修改位置**：insertZRATKOUJI_INF 方法中，约第 146 行至 149 行。
**改修说明**：目前代码在第146行硬编码了 map.put("title", "納税通知書 (事業所用)")，在第149行硬编码提取 JIGYOSYO_KANJI 作为姓名。我们需要将其改为根据传入的 selectId 动态判断。
**具体代码**：
```java
// ====== 【修改前】 (image_65.png 第146行 - 149行) ======
// map.put("title", "納税通知書 (事業所用)"); // 帳票名
// map.put("kojinNo", ZAAUtil.getStringValue(paramMap, "SHITEI_NO"));// 納税者番号
// map.put("sofusakiKojinNo", ZAAUtil.getStringValue(paramMap, "SHITEI_NO"));// 送付先個人番号
// map.put("shimei", ZAAUtil.getStringValue(paramMap, "JIGYOSYO_KANJI"));// 発送先宛名


// ====== 【修改后】 ======
// 1. 动态判断账票名称 (title)
String selectId = tsokujiParam.getSelectId();
String reportTitle = "納税通知書 (事業所用)"; // 默认兜底值

if ("508".equals(selectId)) {
    reportTitle = "特徴納税通知書 (個人用)";
} else if ("509".equals(selectId)) {
    reportTitle = "特徴変更通知書 (個人用)";
} else if ("514".equals(selectId)) {
    reportTitle = "特徴納税通知書 (事業所用)";
} else if ("515".equals(selectId)) {
    reportTitle = "特徴変更通知書 (事業所用)";
}
map.put("title", reportTitle); // 帳票名

map.put("kojinNo", ZAAUtil.getStringValue(paramMap, "SHITEI_NO"));// 納税者番号
map.put("sofusakiKojinNo", ZAAUtil.getStringValue(paramMap, "SHITEI_NO"));// 送付先個人番号

// 2. 兼容提取姓名 (shimei)
// 优先取事业所汉字名，如果没有（即为空，说明是個人用），则取个人姓名
String shimei = ZAAUtil.getStringValue(paramMap, "JIGYOSYO_KANJI");
if (shimei == null || shimei.trim().isEmpty()) {
    // 【重点注意】：请确认您系统 SQL 返回的个人姓名字段到底是叫 "SHIMEI_KANJI" 还是别的名字，这里以 "SHIMEI_KANJI" 举例
    shimei = ZAAUtil.getStringValue(paramMap, "SHIMEI_KANJI"); 
}
map.put("shimei", shimei);// 発送先宛名

```
完成这两步修改后，画面的复选框会被正确激活，同时后端插入到数据库中的“返还信息记录”，其账票标题和收件人姓名也不会再出现全部变成“事业所用”和空名的数据错误了。
