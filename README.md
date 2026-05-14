既然普徴的问题已经解决，我们将目光完全聚焦在**特徴（特别征收）**的缺陷上。
根据工单 QA #28598 的描述，关于特徴的故障表现如下：
 * 特徴変更通知書（個人用）可以正常勾选并注册。
 * **特徴納税通知書（事業所用・個人用）** 以及 **特徴変更通知書（事業所用）** 的复选框处于非活性状态，无法勾选，导致无法注册返还信息。
结合您提供的代码，以下是针对**特徴（Tokucho）**的故障原因分析及详细改修步骤。
### 🐞 故障原因分析（特征篇）
 1. **前端复选框被硬编码锁死（导致现象：复选框非活性）**
   在 ZAB002S004.jsp 文件的 doPrintChange(obj) 函数中（见 image_15.png），代码存在严重的硬编码限制。
   逻辑明确写死：只有当 selectId == 509（即特徴変更通知書-個人用）时，henreiCheckValue（返戻情報登録有無）复选框才会被激活（disabled = false）。选择其他所有的特征账票（如508、514、515等），都会走入 else 分支，被强制置灰并取消勾选。这就是为什么工单里提到只有 509 能点的原因。
 2. **后端插入数据时账票名称与抬头被写死（潜在的数据错误）**
   即便前端放开了勾选限制，后端在执行注册时也存在硬编码问题。在 ZAB002S004_TsokujiDaoImpl.java 的 insertZRATKOUJI_INF 方法中（见 image_41.png）：
   * map.put("title", "納税通知書 (事業所用)");：无论打印哪种特征账票，插入数据库的标题永远被写死成“事业所用”。
   * map.put("shimei", ZAAUtil.getStringValue(paramMap, "JIGYOSYO_KANJI"));：姓名只获取事业所汉字名。如果是“個人用”的账票，这里极有可能取不到值，导致注册数据的姓名为空。
### 🛠️ 详细改修步骤（特征篇）
需要对前端 JS 控制逻辑和后端数据组装逻辑进行同步修改。
#### 步骤 1：前端解除特征账票的 Checkbox 禁用限制
**目标文件**：ZAB002S004.jsp (参考 image_15.png 第 517 行附近 doPrintChange 方法)
将原本只允许 509 激活的逻辑，扩展到所有特征类对象账票。通常特征相关的 selectId 包括：508(特徴納税-個人用), 509(特徴変更-個人用), 514(特徴納税-事業所用), 515(特徴変更-事業所用)。
**修改方案**：将 henkoOption 的控制与 henreiCheckValue 的控制解耦。
```javascript
// 保留原有针对 509 特有的 henkoOption 控制
if (i == 509) {
    if (document.ZAB002S004Form.henkoOption) {
        document.ZAB002S004Form.henkoOption.disabled = false;
    }
} else {
    if (document.ZAB002S004Form.henkoOption) {
        document.ZAB002S004Form.henkoOption.disabled = true;
    }
}

// 新增独立的返还信息复选框控制（加入普徴修复时所需的ID，以及特征的 508, 509, 514, 515 等）
var allowedIds = [508, 509, 514, 515]; // 注：请将您在普徴中允许的ID也一并加入此数组
if (allowedIds.includes(parseInt(i))) {
    if (document.ZAB002S004Form.henreiCheckValue) {
        document.ZAB002S004Form.henreiCheckValue.disabled = false;
    }
} else {
    if (document.ZAB002S004Form.henreiCheckValue) {
        $("#henreiCheckValue").attr("checked", false);
        document.ZAB002S004Form.henreiCheckValue.disabled = true;
    }
}

```
#### 步骤 2：后端动态生成账票标题与处理个人姓名
**目标文件**：ZAB002S004_TsokujiDaoImpl.java (参考 image_41.png 的 insertZRATKOUJI_INF 方法)
去除写死的 "納税通知書 (事業所用)"，根据前端传来的 selectId 动态判断账票名称；同时兼容获取“個人用”情况下的纳税人姓名。
**修改方案**：
```java
private void insertZRATKOUJI_INF(Map<String, Object> paramMap, ZAB002S004_TsokujiParam tsokujiParam) {
    // ... 前置代码保持不变 ...

    // 1. 动态判断账票名称 (title)
    String selectId = tsokujiParam.getSelectId();
    String reportTitle = "納税通知書 (事業所用)"; // 默认值
    
    if ("508".equals(selectId)) {
        reportTitle = "特徴納税通知書 (個人用)";
    } else if ("509".equals(selectId)) {
        reportTitle = "特徴変更通知書 (個人用)";
    } else if ("514".equals(selectId)) {
        reportTitle = "特徴納税通知書 (事業所用)";
    } else if ("515".equals(selectId)) {
        reportTitle = "特徴変更通知書 (事業所用)";
    }
    map.put("title", reportTitle); // 替换原来的 map.put("title", "納税通知書 (事業所用)");

    // 2. 兼容提取姓名 (shimei)
    // 优先取事业所名，若为空(说明是個人用)，则取个人姓名
    String shimei = ZAAUtil.getStringValue(paramMap, "JIGYOSYO_KANJI");
    if (StringUtils.isEmpty(shimei)) {
        // 替换 "SHIMEI_KANJI" 为实际 SQL 中个人姓名的列名，如 "KOJIN_SHIMEI" 等
        shimei = ZAAUtil.getStringValue(paramMap, "SHIMEI_KANJI"); 
    }
    map.put("shimei", shimei); // 替换原来的 map.put("shimei", ZAAUtil.getStringValue(paramMap, "JIGYOSYO_KANJI"));

    // ... 后续 insert 逻辑保持不变 ...
}

```
