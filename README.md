DECLARE
  v_src CLOB;
BEGIN
  -- 锁定原始 CLOB 以供更新
  SELECT clob_col INTO v_src FROM your_table WHERE id = 1 FOR UPDATE;

  -- 直接使用 REPLACE 替换中文逗号
  v_src := REPLACE(v_src, '，', '');

  -- 写回原字段（Oracle CLOB 是引用类型，直接替换即可）
  UPDATE your_table
     SET clob_col = v_src
   WHERE id = 1;

END;