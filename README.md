-- 找到 2025/03/12 S.Kyou Update 的位置 (约 3664行)
-- 修改逻辑如下：

IF RKZ.NENDOBUN >= 2026 THEN  -- 针对令和8年度及以后
    
    -- 核心修正：在计算总人数时，必须加上 NVL(NKOHAI_TAISHOSHA, 0)
    IF RKZ.FUYO_HIKYOJYU > (
        NVL(NKOHAI_TAISHOSHA, 0) +     -- 加上控除对象配偶者 (1或0)
        NVL(RKZ.FUYO_IPN, 0) +         -- 一般扶养
        NVL(RKZ.FUYO_TOKUTEI, 0) +     -- 特定扶养
        NVL(RKZ.FUYO_ROJIN_GOKEI, 0) + -- 老人扶养
        NVL(RKZ.FUYO_NENSHO, 0)        -- 年少扶养
    ) 
    -- 另外，如果勾选了非居住配偶者，但配偶者本身不是控除对象，也需要额外判断
    OR (NVL(RKZ.HAIHIKYO_KBN, 0) > 0 AND NVL(NKOHAI_TAISHOSHA, 0) = 0)
    THEN
        PROC_ERR_TOROKU(1879);
    END IF;

END IF;
