INSERT INTO your_table (id, name)
SELECT rownum, t.name
  FROM your_table t, (SELECT 1 FROM dual CONNECT BY LEVEL <= 10000);