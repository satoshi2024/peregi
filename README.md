SELECT 
    s.sid,
    s.serial#,
    s.username,
    s.module,
    s.action,
    s.program,
    s.plsql_entry_object_id,
    s.plsql_object_id
FROM v$session s
WHERE s.status = 'ACTIVE';