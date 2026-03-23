SELECT 
    -- 第三步：只在序号为 1 的行显示数据，2和3显示为空
    CASE WHEN b.lvl = 1 THEN TO_CHAR(a.KOJIN_NO) ELSE NULL END AS KOJIN_NO
FROM (
    -- 第一步：执行你原本的查询
    SELECT KOJIN_NO, ROWID as rid
    FROM gabtatenakihon
    WHERE KOJIN_NO BETWEEN 1210003747 AND 1210005246
) a
CROSS JOIN (
    -- 第二步：构造一个包含 3 行的临时序列 (1, 2, 3)
    SELECT LEVEL as lvl FROM DUAL CONNECT BY LEVEL <= 3
) b
-- 最后按照原始顺序和行号排序，保证空白行紧跟在数据行后面
ORDER BY a.rid, b.lvl;
