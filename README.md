    -- ★★★ QA#28908 对应 追加 Start ★★★
    IF NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 4 THEN
        -- 強制課税区分 = 4 (租税条約（非課税）) の場合
        R_ZAAW010o008WK.KAZEIHIKBN := '0';    -- 課税非課税区分：0 (非課税)
        R_ZAAW010o008WK.HIKAZEI_KBN := '08';  -- 非課税判定区分：08 (租税条約非課税)

    ELSIF NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 5 THEN
        -- 強制課税区分 = 5 (租税条約（免税）) の場合
        R_ZAAW010o008WK.KAZEIHIKBN := '1';    -- 課税非課税区分：1 (課税)
        R_ZAAW010o008WK.HIKAZEI_KBN := '00';  -- 非課税判定区分：00 (非該当 ＝ 均等割・所得割ともに課税)

    ELSE
    -- ★★★ QA#28908 对应 追加 End ★★★

        -- =========================================================
        -- ▼▼▼ 原本的课税非课税判定、非课税判定区分的逻辑 (原 1935 行以后) ▼▼▼
        -- =========================================================
        -- IF RDATA.NENZEIGAKU > 0 THEN ...
        -- ... (这里保留你原来写的长串 IF ELSE) ...
        -- ...
        -- R_ZAAW010o008WK.HIKAZEI_KBN := LPAD(RDATA.HIKAZEI_KBN, 2, '0');
        -- IF NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 1 THEN ...
        -- ...

    END IF; -- 别忘了最后给新增的 IF 加上 END IF;
