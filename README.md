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
        ROW_NUMBER() OVER (ORDER BY G.KOJIN_NO) AS rn 
    FROM ZABTGANTAN G, GABTATENAKIHON A
    WHERE G.KOJIN_NO = A.KOJIN_NO
      AND G.NENDO = 2026
      AND A.GENZON_KBN = 0
      AND G.SEINENGAPI > 0
      AND REGEXP_LIKE(G.SHIMEI_KANA, '^[ア-ヶ ]+$')
      AND LENGTH(G.SHIMEI_KANA) <= 20
      AND G.KOJIN_NO NOT IN (SELECT KOJIN_NO FROM ZABTKAZEI WHERE NENDOBUN = 2026)
),
TotalCount AS (
    SELECT COUNT(*) as cnt FROM MyOriginalQuery
)
SELECT KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA
FROM (
    -- 1. 业务数据：映射到每个周期的 3-12 位
    SELECT 
        KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA,
        -- 公式：((n-1)/10)*12 + (n-1)%10 + 3
        -- 这样：rn=1时key=3, rn=10时key=12, rn=11时key=15
        FLOOR((rn - 1) / 10) * 12 + MOD(rn - 1, 10) + 3 AS sort_key
    FROM MyOriginalQuery

    UNION ALL

    -- 2. 空行数据：占据每个周期的 1-2 位
    SELECT 
        NULL, NULL, NULL, NULL, NULL,
        (level_num * 12) + offset_num AS sort_key
    FROM (
        SELECT LEVEL - 1 AS level_num FROM DUAL 
        CONNECT BY LEVEL <= (SELECT (cnt/10) + 1 FROM TotalCount)
    )
    CROSS JOIN (SELECT 1 AS offset_num FROM DUAL UNION ALL SELECT 2 FROM DUAL)
)
-- 过滤掉最后多余的空行，确保结果整齐
WHERE sort_key <= (SELECT FLOOR((cnt-1)/10)*12 + MOD(cnt-1,10) + 3 FROM TotalCount)
ORDER BY sort_key;
