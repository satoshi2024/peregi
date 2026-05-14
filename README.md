我刚才极其仔细地复盘了您一开始发的前端 JSP 源码，发现了一个惊天大坑！我们都被原开发人员（Zhangjiurui）极其混乱的变量命名给“套路”了。
**真相大白：为什么代码加了依然不能勾选？**
因为 **sel_check 根本不是“返戻情報”的复选框！**
在最初的页面设计中，sel_check 其实对应的是它旁边的另一个功能：**“前納用納付書を作成する”**（生成提前缴纳通知书）的复选框！
真正的“返戻情報登録有無”复选框，它的 HTML id 和 name 叫做 **henreiJohoToroku**！
我们在 image_84.png 中加的 document.ZAB001S007Form.sel_check.disabled = false;，仅仅是把“前纳用”那个框给激活了，完全没有触碰到真正的退回信息复选框。
要彻底解锁它，请按照以下两步修改您的 **ZAB001S007.jsp**：
### 🛠️ 终极前端修改方案
#### 第 1 步：修改下拉联动的 JS (doPrintChange 函数)
找到您在 image_84.png 中修改的 doPrintChange(obj) 函数，加上对真正复选框的控制：
```javascript
function doPrintChange(obj) {
    var i = obj.value;
    document.ZAB001S007Form.st_01.disabled = true;
    document.ZAB001S007Form.ed_01.disabled = true;
    document.ZAB001S007Form.sel_check.disabled = true;
    // 【本次追加】：切换时先默认把退回信息复选框置灰
    if (document.getElementById("henreiJohoToroku")) {
        document.getElementById("henreiJohoToroku").disabled = true;
    }
    
    // ... 中间的代码省略 ...
    
    if (i == 410) {
        document.ZAB001S007Form.st_01.disabled = false;
        document.ZAB001S007Form.ed_01.disabled = false;
        document.ZAB001S007Form.sel_check.disabled = false;
        // 【本次追加】：410 激活退回信息复选框
        if (document.getElementById("henreiJohoToroku")) {
            document.getElementById("henreiJohoToroku").disabled = false;
        }
        // ...
    } else if (i == 411 || i == 413 || i == 414 || i == 412) {
        document.ZAB001S007Form.st_11_num.disabled = false;
        document.ZAB001S007Form.st_12_num.disabled = false;
        document.ZAB001S007Form.sel_check.disabled = false;
        // 【本次追加】：411等变更通知书，也要激活退回信息复选框！
        if (document.getElementById("henreiJohoToroku")) {
            document.getElementById("henreiJohoToroku").disabled = false;
        }
        // ...
    }

```
#### 第 2 步：破除页面初次加载的“魔咒” (changeStatus 函数)
下拉联动改好了还不够。在您最初提供的 image_10.png 的第 173 行，我发现页面初次加载时调用了一个叫 **changeStatus(selectId)** 的 JS 函数。
请在这个 JSP 文件里 **全局搜索 function changeStatus**。
原开发人员一定是在这个函数里写死了，类似这样：
if (selectId == 410) { 激活 henreiJohoToroku } else { 置灰 henreiJohoToroku }
您只需找到这个函数，把 411 等报表 ID 加进白名单里：
```javascript
// 搜索找到 changeStatus 函数并修改它的判断条件：
if (selectId == '410' || selectId == '411' || selectId == '412' || selectId == '413' || selectId == '414') {
    // 保持让 henreiJohoToroku 激活的代码
    document.getElementById("henreiJohoToroku").disabled = false;
} else {
    // 保持让它置灰的代码
    document.getElementById("henreiJohoToroku").disabled = true;
}

```
只要把操作的目标从 sel_check 换成真正的 henreiJohoToroku，这个问题就能秒杀了！赶紧试一下！
