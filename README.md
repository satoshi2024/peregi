-- 実行中のセッションを確認
SELECT s.sid, s.serial#, s.username, s.program, s.status,
       s.sql_id, sq.sql_text
FROM v$session s
JOIN v$sql sq ON s.sql_id = sq.sql_id
WHERE s.username = 'ユーザー名'
   OR s.program LIKE '%你的程序%'
   OR sq.sql_text LIKE '%特定のSQL%';