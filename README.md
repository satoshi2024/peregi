-- 检查 均等割额 和 均等割减免额 是否都为 0 (建议使用 NVL 防止 NULL 导致判断失效)
IF NVL(RDATA.SHIM_KINTOWARI, 0) = 0 AND NVL(RDATA.SHIM_GENMEN_KINWARI, 0) = 0 THEN
    -- 如果满足全额减免/无均等割条件，直接赋值为 0
    R_ZAAW010o008WK.SHIM_SHOTOKUWARI := 0;
ELSE
    -- 否则走原本的计算逻辑 (保留端数计算)
    R_ZAAW010o008WK.SHIM_SHOTOKUWARI := FUNC_KOMOKU_EDIT(c_KASAN,
                                          FUNC_KOMOKU_EDIT(c_GENSAN, RDATA.SHIM_SHOTOKUWARI, RDATA.SHIM_GENMEN_SHOWARI),
                                          RDATA.SHIM_HASU);
END IF;
