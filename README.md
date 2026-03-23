SELECT * FROM table(DBMS_XPLAN.DISPLAY('stats$sql_plan', NULL, 'ALL', 'hash_value = 1086119684'));
