DECLARE
  v_src      CLOB;
  v_dst      CLOB;
  v_chunk    VARCHAR2(4000);
  v_pos      INTEGER := 1;
  v_amount   INTEGER := 4000;
  v_len      INTEGER;
BEGIN
  -- 获取原始 CLOB
  SELECT clob_col INTO v_src FROM your_table WHERE id = 1 FOR UPDATE;

  -- 创建一个空的 CLOB 用于接收替换后的内容
  DBMS_LOB.CREATETEMPORARY(v_dst, TRUE);

  -- 获取长度
  v_len := DBMS_LOB.getlength(v_src);

  WHILE v_pos <= v_len LOOP
    -- 读取一段
    DBMS_LOB.READ(v_src, v_amount, v_pos, v_chunk);

    -- 替换中文逗号
    v_chunk := REPLACE(v_chunk, '，', '');

    -- 写入到新 CLOB
    DBMS_LOB.WRITEAPPEND(v_dst, LENGTH(v_chunk), v_chunk);

    -- 下一段
    v_pos := v_pos + v_amount;
  END LOOP;

  -- 清空旧内容，写入新内容
  DBMS_LOB.TRIM(v_src, 0);
  DBMS_LOB.WRITEAPPEND(v_src, DBMS_LOB.GETLENGTH(v_dst), v_dst);

  -- 释放临时 CLOB
  DBMS_LOB.FREETEMPORARY(v_dst);
END;
