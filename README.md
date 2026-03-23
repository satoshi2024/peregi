SELECT 
    id,
    MAX(original_column) OVER (PARTITION BY grp) as filled_column
FROM (
    SELECT 
        id,
        original_column,
        COUNT(original_column) OVER (ORDER BY id) as grp
    FROM your_table_name
);
