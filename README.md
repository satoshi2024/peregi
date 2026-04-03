SELECT s.sid, s.serial#, s.username, o.object_name
FROM v$locked_object l
JOIN dba_objects o ON l.object_id = o.object_id
JOIN v$session s ON l.session_id = s.sid;