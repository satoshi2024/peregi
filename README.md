    -- ★★★ 2026/04/XX QA#28908対応 追加 Start ★★★
    IF NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 4 THEN
        -- 強制課税区分 = 4 (租税条約（非課税）) の場合
        R_ZAAW010o008WK.KAZEIHIKBN := '0';    -- 非課税
        R_ZAAW010o008WK.HIKAZEI_KBN := '08';  -- 租税条約非課税

    ELSIF NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 5 THEN
        -- 強制課税区分 = 5 (租税条約（免税）) の場合
        R_ZAAW010o008WK.KAZEIHIKBN := '1';    -- 課税
        R_ZAAW010o008WK.HIKAZEI_KBN := '00';  -- 非該当 (均等割・所得割ともに課税)

    ELSE
    -- ★★★ 2026/04/XX QA#28908対応 追加 End ★★★

        -- =========================================================
        -- ▼▼▼ 原有逻辑保持不变 (原 1934 行 - 1964 行) ▼▼▼
        -- =========================================================
        --課税非課税区分
        --2025/04/14 JIP.KOYABU Upd
        IF RDATA.NENZEIGAKU > 0 THEN
            -- ... (原有的长串条件判断) ...
            R_ZAAW010o008WK.KAZEIHIKBN := '1';
        ELSE
            R_ZAAW010o008WK.KAZEIHIKBN := '0';
        END IF;

        --非課税判定区分
        --JIP.KOYABU 2025/02/04 Upd str
        R_ZAAW010o008WK.HIKAZEI_KBN := LPAD(RDATA.HIKAZEI_KBN, 2, '0');
        IF NVL(RDATA.KYOSEI_KAZEI_KBN, 0) = 1 THEN
            R_ZAAW010o008WK.HIKAZEI_KBN := '06';
        ELSE
            -- ... (原有的长串处理) ...
        END IF;
        -- =========================================================

    -- ★★★ 2026/04/XX QA#28908対応 追加 Start ★★★
    END IF;
    -- ★★★ 2026/04/XX QA#28908対応 追加 End ★★★
