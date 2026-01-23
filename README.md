# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_text_to_add):
    # --- 逻辑 1: 在 CREATE TABLE 之后插入字段 ---
    # 增加 \s* 以处理可能的空格
    pattern_create = r"(create\s+table\s+\w+\s*\()"
    replacement_create = r"\1" + fields_to_add + ", "
    content = re.sub(pattern_create, replacement_create, content, flags=re.IGNORECASE)

    # --- 逻辑 2: 在第一个 COMMENT ON TABLE ... / 下方添加文言 ---
    # 优化后的正则：匹配 COMMENT...IS ''; 然后跨越任何空格/换行匹配到 /
    # [\s\S]*? 表示匹配包括换行符在内的任意字符，直到遇到第一个 /
    pattern_comment = r"(COMMENT\s+ON\s+TABLE\s+\w+\s+IS\s+'';\s*[\r\n]+\s*/)"
    
    # 替换逻辑：保留原匹配项(\1)，换行，然后添加新文言
    replacement_comment = r"\1\n" + comment_text_to_add
    
    # 使用 count=1 确保只修改第一个匹配项
    if re.search(pattern_comment, content, flags=re.IGNORECASE):
        content = re.sub(pattern_comment, replacement_comment, content, count=1, flags=re.IGNORECASE)
        print(" -> 已成功定位并修改 COMMENT 部分")
    else:
        print(" -> 未找到匹配的 COMMENT 语句，请检查 SQL 格式")
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_msg):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            try:
                # 按照你的要求：UTF-8 读取
                with open(input_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # 执行修改
                new_content = modify_sql_content(content, fields, comment_msg)

                # 按照你的要求：Shift-JIS 写入
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                
                print(f"✅ 处理成功: {filename}")
            except Exception as e:
                print(f"❌ 处理失败 {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 请确保使用三引号包裹
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 请确保使用三引号包裹你截图中的长篇 COMMENT 内容
new_comment_msg = '''COMMENT ON COLUMN ZABWL201R207.KYOTSU_TSUSU_RENBAN IS '業務共通_通数連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_PAGE_RENBAN IS '業務共通_頁連番'
/''' # 此处根据你的截图继续粘贴完整

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, new_comment_msg)
        print("\n全部处理完毕！")
    else:
        print(f"找不到目录: {input_dir}")
