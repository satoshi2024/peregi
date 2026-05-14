太精彩了！结合您最新发来的这两张图片以及前面所有的代码拼图，这个困扰 QA 的“未解之谜”终于被彻底揭开了！
我们之前在 Controller 层追踪到参数没有丢，现在在 ZAB001S007_FsokujiDaoImpl.java 的 CallPrintRun 方法中，我找到了**绝对的“案发现场”**。
这根本不是参数没传进去的问题，而是一个非常经典的**“代码执行顺序错位（时序Bug）”**！
以下是详细的破案报告和修复方案：
### 🕵️ 核心真凶：一段“抢跑”的代码
请您把目光锁定在 **image_70.png 和 image_71.png 的第 550 行到 582 行**。
这段代码是 DIS.Zhangjiurui 在 2024/05/07 添加的新功能，专门用来处理“返戻情報登録”（退回信息注册）：
```java
// 第552行: 校验前端是否勾选了复选框 (值为"1")
if ("1".equals(searchInfo.getHenreiJohoToroku())) {
    ...
    // 第566行: 去数据库查询刚刚生成的通知书数据 (ZABWFUCHBOOK)
    List<Map<String, Object>> listMap = getZABWFUCHBOOK(searchInfo); 
    if (listMap != null && !listMap.isEmpty()) {
        for (...) {
            // 第572行: 执行最终的 INSERT，把退回信息落库！
            zab0010Repository.insertZRATKOUJI_INF_067(insertMap); 
        }
    }
}

```
**看逻辑是不是很完美？前端勾了，后台也 if 进去了，也写了 insert。那为什么存不上？**
因为这段代码**执行得太早了**！
在这个即时发行（Online Print）的业务流中，真正负责在数据库里生成“通知书数据”的核心引擎，是底层的 PL/SQL 存储过程。请看 **image_73.png 的第 649 行**：
```java
// 第649行: 执行真正的在线打印 PL/SQL，生成业务数据
rtnFlg = kka000CommonDao.execOnlinePrint(chohyoParam); 

```
### 💥 Bug 的致命逻辑链
 1. 代码跑到第 566 行，试图调用 getZABWFUCHBOOK() 去查通知书数据。
 2. 但是此时，第 649 行的 execOnlinePrint **还没有执行**！数据库里根本没有这次打印的底表数据。
 3. 因此，listMap **永远是空的**。
 4. 第 568 行的 if (!listMap.isEmpty()) 直接进不去，循环跳过，第 572 行的 insert 被完美避开。
 5. 等到第 649 行 PL/SQL 终于执行完、数据有了，代码却直接 return 结束了。
**这就好比：顾客还没开始点菜，服务员就跑到厨房去端菜，端不到菜自然就什么都不上了。**
这同时完美解释了 QA 的疑问：**为什么“特征变更通知书”就能存上？**
因为开发人员在写“特征（特别征收）”的 DaoImpl 时，肯定把这段 insert 逻辑老老实实地放在了 execOnlinePrint 之后，而写“普征”画面时，放错了位置。
### 🛠️ 终极修复方案指引
要完美平息 QA #28598 这个工单，您需要分两步走：
#### 第一步：修复“普征纳税通知书 (410)”存不上的 Bug（后端）
在 ZAB001S007_FsokujiDaoImpl.java 中：
 1. 选中第 550 行到 582 行（就是 DIS.Zhangjiurui Add start 到 Add end 的那整个 Block）。
 2. 将它们**剪切**。
 3. 往下翻，找到 PL/SQL 执行成功后的位置。比如在 **image_73.png 的第 655 行 if (rtnFlg) { reportUri = "SUCCESS"; ... } 内部**，或者在所有 execOnlinePrint 执行完毕且确认 rtnFlg == true 准备组合 returnVector 之前。
 4. 将这段代码**粘贴**过去。
 5. *注意：调整位置时，请确保 searchInfo 等上下文变量在新的位置依然有效。*
#### 第二步：响应 QA 诉求，放开其他报表的复选框（前/后端配合）
既然 QA 要求“普征变更通知书 (411)”等报表也要能注册：
 1. **改前端 (JSP)**：回到 ZAB001S007.jsp 的 doPrintChange 函数，把对应报表 ID 的分支里加上 sel_check.disabled = false;（我们之前分析过的步骤）。并在 HTML 的 <c:if> 中放开对应的渲染限制。
 2. **改后端 (DaoImpl)**：确认移动后的那段落库代码中，getZABWFUCHBOOK() 方法（image_74.png 第 716 行）内部的 SQL (selectZABWFUCHBOOK_066) 是否兼容 411 这种变更通知书的查询。如果变更通知书的数据存在另一张表里，您需要在这里加个 if (chohyoNo.equals("411")) 去查对应的表。
问题已彻底定位！您可以直接拿着这个结论去修改代码并准备提交测试了。祝您杀虫顺利！如果修改过程中对落库位置还有疑虑，随时沟通。
