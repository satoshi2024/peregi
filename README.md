WITH BaseData AS (
    -- 1. 这是你原本的查询逻辑，先加上行号 rn
    SELECT 
        G.KOJIN_NO,
        G.NENDO,
        UTL_I18N.TRANSLITERATE(G.SHIMEI_KANA, 'kana_hwkatakana') AS KANA,
        G.SEINENGAPI,
        KKAPK0020.FNENDO(SUBSTR(G.SEINENGAPI, 1, 4), 0)
            || SUBSTR(G.SEINENGAPI, 5, 2)
            || SUBSTR(G.SEINENGAPI, 7, 2) AS SEINENGAPI_WA,
        ROW_NUMBER() OVER (ORDER BY G.KOJIN_NO) AS rn -- 这里根据个人编号排序
    FROM ZABTGANTAN G, GABTATENAKIHON A
    WHERE G.KOJIN_NO = A.KOJIN_NO
      AND G.NENDO = 2026
      AND A.GENZON_KBN = 0
      AND G.SEINENGAPI > 0
      AND REGEXP_LIKE(G.SHIMEI_KANA, '^[ア-ケ ]+$')
      AND LENGTH(G.SHIMEI_KANA) <= 20
      AND G.KOJIN_NO NOT IN (SELECT KOJIN_NO FROM ZABTKAZEI WHERE NENDOBUN = 2026)
),
ExpandedData AS (
    -- 2. 原始数据：计算排序键 (每10条占位12个)
    -- 第1-10条占据 sort_key 1-10
    SELECT 
        KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA,
        ((rn - 1) / 10) * 12 + MOD(rn - 1, 10) + 1 AS sort_key
    FROM BaseData

    UNION ALL

    -- 3. 插入空行：每组补上第 11 和 12 个位置
    -- 这里的循环次数取决于你的数据量。假设数据不超过 10000 行
    SELECT 
        NULL, NULL, NULL, NULL, NULL, -- 字段数必须与上面一致
        (v.n * 12) + offset AS sort_key
    FROM (
        -- 生成 0 到 1000 的序列（根据需要调整）
        SELECT LEVEL - 1 AS n FROM DUAL CONNECT BY LEVEL <= (SELECT (COUNT(*)/10)+1 FROM BaseData)
    ) v
    CROSS JOIN (SELECT 11 AS offset UNION ALL SELECT 12 AS offset) o
)
-- 4. 最终输出，过滤掉超出实际数据范围的冗余空行
SELECT KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA
FROM ExpandedData
WHERE sort_key <= (SELECT (CEIL(MAX(rn)/10)*12) FROM BaseData)
ORDER BY sort_key;
