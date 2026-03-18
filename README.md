WITH BaseData AS (
    -- 1. 你的原始业务逻辑
    SELECT 
        G.KOJIN_NO,
        G.NENDO,
        UTL_I18N.TRANSLITERATE(G.SHIMEI_KANA, 'kana_hwkatakana') AS KANA,
        G.SEINENGAPI,
        KKAPK0020.FNENDO(SUBSTR(G.SEINENGAPI, 1, 4), 0)
            || SUBSTR(G.SEINENGAPI, 5, 2)
            || SUBSTR(G.SEINENGAPI, 7, 2) AS SEINENGAPI_WA,
        -- 生成原始行号
        ROW_NUMBER() OVER (ORDER BY G.KOJIN_NO) AS rn 
    FROM ZABTGANTAN G, GABTATENAKIHON A
    WHERE G.KOJIN_NO = A.KOJIN_NO
      AND G.NENDO = 2026
      AND A.GENZON_KBN = 0
      AND G.SEINENGAPI > 0
      AND REGEXP_LIKE(G.SHIMEI_KANA, '^[ア-ケ ]+$')
      AND LENGTH(G.SHIMEI_KANA) <= 20
      AND G.KOJIN_NO NOT IN (SELECT KOJIN_NO FROM ZABTKAZEI WHERE NENDOBUN = 2026)
),
MaxRows AS (
    -- 计算总组数，用于生成对应的空行
    SELECT COUNT(*) as total_cnt, CEIL(COUNT(*)/10) as total_groups FROM BaseData
),
GenerateKeys AS (
    -- 2. 原始数据：计算它在“12行一个周期”里的位置 (1-10)
    SELECT 
        KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA,
        ((rn - 1) / 10) * 12 + MOD(rn - 1, 10) + 1 AS sort_key
    FROM BaseData
    
    UNION ALL
    
    -- 3. 构造空行：每个周期里的第 11 和 12 位
    SELECT 
        NULL, NULL, NULL, NULL, NULL,
        (g.n * 12) + offset AS sort_key
    FROM (
        -- 生成足够的组序号 (0, 1, 2...)
        SELECT LEVEL - 1 AS n FROM DUAL 
        CONNECT BY LEVEL <= (SELECT total_groups FROM MaxRows)
    ) g
    CROSS JOIN (SELECT 11 AS offset FROM DUAL UNION ALL SELECT 12 FROM DUAL) o
)
-- 4. 最终输出并排序
SELECT 
    KOJIN_NO, NENDO, KANA, SEINENGAPI, SEINENGAPI_WA
FROM GenerateKeys
-- 过滤掉最后多出来的无效空行
WHERE sort_key <= (SELECT ((total_groups - 1) * 12 + 12) FROM MaxRows)
ORDER BY sort_key;
