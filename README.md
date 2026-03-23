SELECT 
    filled_kojin_no
FROM (
    -- 第三步：在每个生成的组内，取非空值（即最大值）填满空白
    SELECT 
        MAX(a.KOJIN_NO) OVER (PARTITION BY grp) AS filled_kojin_no
    FROM (
        -- 第一步：使用 ROWID 排序，为每一行数据生成组号 (grp)
        -- COUNT(KOJIN_NO) 遇到非空数据会计数+1，遇到空值计数不变，从而将空值划入上一行数据的组
        SELECT 
            a.KOJIN_NO,
            COUNT(a.KOJIN_NO) OVER (ORDER BY a.ROWID) AS grp
        FROM gabtatenakihon a
    ) a
)
-- 第二步：在填充完毕后，再进行范围筛选
-- 注意：必须在填充后的结果上筛选，否则中间的空白行会被原生的 BETWEEN 过滤掉
WHERE filled_kojin_no BETWEEN 1210003747 AND 1210005246;
