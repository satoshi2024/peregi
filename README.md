SELECT sql_id, 
       child_number, 
       plan_hash_value, 
       executions, 
       buffer_gets,
       elapsed_time / 1000000 AS elapsed_sec
FROM v$sql 
WHERE hash_value = 4160013739;-- 请将下面的 '您的SQL_ID' 替换为步骤1中查到的真实 sql_id
SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR('您的SQL_ID', NULL, 'ADVANCED ALLSTATS LAST'))SELECT id, 
       LPAD(' ', depth) || operation AS operation, 
       options, 
       object_name, 
       cost, 
       cardinality, 
       bytes
FROM stats$sql_plan
WHERE plan_hash_value = (
    SELECT MAX(plan_hash_value) 
    FROM stats$sql_summary 
    WHERE hash_value = 4160013739
)
ORDER BY id;


