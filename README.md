SELECT 
    s.sid,
    s.serial#,
    s.username,
    s.program,
    s.status,
    s.sql_id,
    q.sql_text
FROM v$session s
LEFT JOIN v$sql q 
    ON s.sql_id = q.sql_id
WHERE s.status = 'ACTIVE';