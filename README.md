WITH MyOriginalQuery AS (
    -- 这里是你图片中完全不动的原始逻辑
    SELECT 
        G.KOJIN_NO,
        G.NENDO,
        UTL_I18N.TRANSLITERATE(G.SHIMEI_KANA, 'kana_hwkatakana') AS KANA,
        G.SEINENGAPI,
        KKAPK0020.FNENDO(SUBSTR(G.SEINENGAPI, 1, 4), 0)
            || SUBSTR(G.SEINENGAPI, 5, 2)
            || SUBSTR(G.SEINENGAPI, 7, 2) AS SEINENGAPI_WA,
        -- 只增加这一行用来计数的行号
        ROW_NUMBER() OVER (ORDER BY G.KOJIN_NO) AS rn 
    FROM ZABTGANTAN G, GABTATENAKIHON A
    WHERE G.KOJIN_NO = A.KOJIN_NO
      AND G.NENDO = 2026
      AND A.GENZON_KBN = 0
      AND G.SEINENGAPI > 0
      AND REGEXP_LIKE(G.SHIMEI_KANA, '^[ア-ヶ ]+$') -- 建议改为ア-ヶ以包含全部片假名
      AND LENGTH(G.SHIMEI_KANA) <= 20
      AND G.KOJIN_NO NOT IN (SELECT KOJIN_NO FROM ZABTKAZEI WHERE NENDOBUN = 2026)
)
-- 下面是专门负责“插空行”的显示逻辑，不影响上面的筛选
SELECT 
    KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA
FROM (
    -- 抽出原始数据并计算位置
    SELECT 
        KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA,
        ((rn - 1) / 10) * 12 + MOD(rn - 1, 10) + 1 AS sort_key
    FROM MyOriginalQuery

    UNION ALL

    -- 插入空行
    SELECT 
        NULL, NULL, NULL, NULL, NULL,
        (level_num * 12) + offset_num AS sort_key
    FROM (
        SELECT LEVEL - 1 AS level_num FROM DUAL 
        CONNECT BY LEVEL <= (SELECT COUNT(*)/10 + 1 FROM MyOriginalQuery)
    )
    CROSS JOIN (SELECT 11 AS offset_num FROM DUAL UNION ALL SELECT 12 FROM DUAL)
)
ORDER BY sort_key;
