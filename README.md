-- 2025/03/12 S.Kyou Update start 1.0.100.000:2025年度個人住民税制度改正対応
-- 修正逻辑：令和7/8年度新规，严格分离校验【非居住扶养亲属】与【非居住配偶者】

-- 1. 校验扶养亲属非居住人数是否越界
IF RKZ.FUYO_HIKYOJYU > (NVL(RKZ.FUYO_IPN, 0) + NVL(RKZ.FUYO_TOKUTEI, 0) + NVL(RKZ.FUYO_ROJIN_GOKEI, 0) + NVL(RKZ.FUYO_NENSHO, 0)) 
-- 2. 校验配偶者非居住标志是否越界 (有非居住配偶标志，但却没有控除对象配偶者)
OR NVL(RKZ.HAIHIKYO_KBN, 0) > NVL(NKOHAI_TAISHOSHA, 0) THEN
    
    PROC_ERR_TOROKU(1879);

END IF;
-- 2025/03/12 S.Kyou Update end
