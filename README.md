# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_text_to_add):
    # 逻辑 1: 在 CREATE TABLE 表名( 之后插入字段
    pattern_create = r"(create\s+table\s+\w+\s*\()"
    replacement_create = r"\1" + fields_to_add + ", "
    content = re.sub(pattern_create, replacement_create, content, flags=re.IGNORECASE)

    # 逻辑 2: 在第一个 COMMENT ON TABLE ... / 下方添加文言
    # 匹配模式：COMMENT ON TABLE 任何字符 IS ''; /
    # re.DOTALL 确保 . 可以匹配换行符
    pattern_comment = r"(COMMENT\s+ON\s+TABLE\s+\w+\s+IS\s+'';\s*\n\s*/)"
    replacement_comment = r"\1\n" + comment_text_to_add
    
    # count=1 确保只在第一个匹配项下方添加
    content = re.sub(pattern_comment, replacement_comment, content, count=1, flags=re.IGNORECASE)
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_msg):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            try:
                # 以 UTF-8 读取原始文件
                with open(input_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # 执行修改逻辑
                new_content = modify_sql_content(content, fields, comment_msg)

                # 以 Shift-JIS 写入新文件
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                
                print(f"✅ Success: {filename}")
            except Exception as e:
                print(f"❌ Error {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 1. 要插入的字段 (使用三引号防止引号报错)
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)''' # 这里可以继续添加完

# 2. 要在 COMMENT 下方添加的新文言
new_comment_msg = "-- 这里是新添加的文言内容"

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, new_comment_msg)
        print("\n所有任务已完成！请检查 processed_sql 文件夹。")
    else:
        print(f"错误：找不到文件夹 {input_dir}")
