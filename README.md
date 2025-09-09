-- 第1位/总体输出：一位→值；两位及以上/空/0→空格
VKBN := VKBN ||
  CASE
    WHEN ABC IS NULL OR TRIM(TO_CHAR(ABC)) = '0' THEN ' '
    WHEN LENGTH(TRIM(TO_CHAR(ABC))) = 1 THEN SUBSTR(TRIM(TO_CHAR(ABC)),1,1)
    ELSE ' '
  END;

-- 第2行不追加（保持结构可放空串；在 Oracle 中 || NULL/'' 不改变结果）
VKBN := VKBN || NULL;
