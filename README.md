SELECT 
    t.*,
    ((ROW_NUMBER() OVER (ORDER BY 排序字段) - 1) / 10) AS group_id
FROM your_table t
