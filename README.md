BEGIN
    DELETE FROM your_table t
     WHERE ROWID IN (
        SELECT rid
          FROM (
                SELECT ROWID rid,
                       ROW_NUMBER() OVER (
                           PARTITION BY col1, col2, col3   -- 判断重复的字段
                           ORDER BY renban DESC            -- 按连番倒序
                       ) rn
                  FROM your_table
               )
         WHERE rn > 1
     );
    COMMIT;
END;
/