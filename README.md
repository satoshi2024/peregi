这是一个非常标准的重构过程。小籔前辈的意思是：游标（Cursor）内部会循环执行成千上万次，把取参数的操作放在里面会导致数据库被查询几万次，严重影响性能。我们需要把它移到只执行一次的 **PROC_INIT（初期处理）** 中。
为了实现这个目标，我们需要**分三步**来进行修改：
### 第一步：在主循环中“删除”并“修改”原有逻辑
回到你之前修改的地方（大约在 **1984 行** 附近，也就是主游标循环内部）。
 1. **删除这行代码：**
   ```plsql
   ISTATUS := KKAPK0030.FPRMSHUTOKU('ZAB', 'CHG_KYOSEI_KAZEI_KBN', 0, ACONSPRM, NPRMSUM);
   
   ```
 2. **修改 IF 判断条件：**
   （⚠️ **特别注意：** 你之前截图里写的是 IN ('0', '1')，这是错的，根据需求必须是 ('1', '2')，请顺手改过来！）
   将原来的 IF NPRMSUM > 0 AND ACONSPRM(1) IN ... 替换为使用我们即将定义的全局变量 V_CHG_KYOSEI_KAZEI_FLG：
   ```plsql
        --2026/05/12 S.Cyo Add start 1.1.307.000:保守QA28908対応
        -- 判定改成直接读全局变量 (注意这里必须是 '1', '2')
        IF V_CHG_KYOSEI_KAZEI_FLG IN ('1', '2') AND NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 4 THEN
            -- 強制課税区分 = 4 (租税条約（非課税）) の場合
            R_ZAAW010o008WK.KAZEIHIKBN := '0';    -- 非課税
            R_ZAAW010o008WK.HIKAZEI_KBN := '08';  -- 租税条約非課税
   
        ELSIF V_CHG_KYOSEI_KAZEI_FLG IN ('1', '2') AND NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 5 THEN
            -- 強制課税区分 = 5 (租税条約（免税）) の場合
            R_ZAAW010o008WK.KAZEIHIKBN := '1';    -- 課税
            R_ZAAW010o008WK.HIKAZEI_KBN := '00';  -- 非該当 (均等割・所得割ともに課税)
   
        ELSE
        --2026/05/12 S.Cyo Add end
   
   ```
### 第二步：在文件顶部声明“全局变量”
为了让 PROC_INIT 取到的值能传递给后面的主循环使用，我们需要定义一个全局变量。
请滚动到这个 .SQL 文件的**最上面**（通常在 IS 之后，有很多 V_... 变量声明的地方，大约在 100 行到 200 行左右）。
找个空白行，加入下面这句变量声明：
```plsql
    V_CHG_KYOSEI_KAZEI_FLG  VARCHAR2(1) := '0';  -- QA28908: 強制課税区分パラメータ保持用

```
### 第三步：在 PROC_INIT 中追加取参数的逻辑
根据你发的第 4 张截图，PROC_INIT 结束的地方在 **第 463 行** 附近（END PROC_INIT; 之前）。我们把取参数的动作加在它的最末尾。
请在 **第 463 行** （END PROC_INIT; 的上一行）插入以下代码：
```plsql
    -- ★★★ 2026/05/12 S.Cyo Add start 1.1.307.000:保守QA28908対応 ★★★
    -- QA28908: パラメータ取得 (ZAB, CHG_KYOSEI_KAZEI_KBN)
    -- カーソル内での多重取得を避けるため、初期処理で1回だけ取得する
    ISTATUS := KKAPK0030.FPRMSHUTOKU('ZAB', 'CHG_KYOSEI_KAZEI_KBN', 0, ACONSPRM, NPRMSUM);
    
    IF NPRMSUM > 0 THEN
        V_CHG_KYOSEI_KAZEI_FLG := ACONSPRM(1);
    ELSE
        V_CHG_KYOSEI_KAZEI_FLG := '0';
    END IF;
    -- ★★★ 2026/05/12 S.Cyo Add end ★★★
    
    END PROC_INIT;  -- 这是你原代码的第464行

```
### 🎯 总结一下现在的代码流：
 1. 程序启动。
 2. 调用 PROC_INIT，去查一次数据库，把查到的参数值（比如 '1'）存进了口袋里（V_CHG_KYOSEI_KAZEI_FLG）。
 3. 程序进入游标循环，开始处理 10 万条人员数据。
 4. 每次循环到判断的地方，直接从口袋里拿值（V_CHG_KYOSEI_KAZEI_FLG IN ('1', '2')），**再也不用去查数据库了**。
 5. 性能大幅提升！
修改完成后，记得把代码丢进数据库里**编译 (Compile)** 一下，然后再去跑一遍测试确认结果。确认 OK 就可以回复小籔前辈了！
