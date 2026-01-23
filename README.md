# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_template):
    # 1. 自动提取真实的表名 (Extract real table name)
    # 匹配 create table 后的第一个单词
    table_match = re.search(r"create\s+table\s+([\w\d\"]+)", content, flags=re.IGNORECASE)
    if table_match:
        real_table_name = table_match.group(1).replace('"', '')
        print(f" -> Table detected: {real_table_name}")
    else:
        real_table_name = "UNKNOWN_TABLE"
        print(" -> Warning: Table name not found")

    # 2. 动态替换模板中的表名 (Dynamic replacement)
    # 将模板中的 ZABWL201R207 替换为当前文件真实的表名
    current_comment_msg = comment_template.replace("ZABWL201R207", real_table_name)

    # 3. 逻辑 A: 在 CREATE TABLE 之后插入新字段
    pattern_create = r"(create\s+table\s+[\w\d\"]+\s*\()"
    replacement_create = r"\1" + fields_to_add + ", "
    content = re.sub(pattern_create, replacement_create, content, flags=re.IGNORECASE)

    # 4. 逻辑 B: 关键词定位法在第一个 / 后插入内容
    keyword = "COMMENT ON TABLE"
    start_pos = content.upper().find(keyword)
    
    if start_pos != -1:
        slash_pos = content.find("/", start_pos)
        if slash_pos != -1:
            before_slash = content[:slash_pos + 1]
            after_slash = content[slash_pos + 1:]
            # 插入动态生成的文言
            content = before_slash + "\n" + current_comment_msg + after_slash
            print(f" -> Success: Applied dynamic comments for {real_table_name}")
        else:
            print(" -> Error: Slash '/' not found after COMMENT keyword")
    else:
        print(" -> Skip: No COMMENT ON TABLE found")
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_temp):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # 自动尝试多种编码读取原始 SQL (Try multiple encodings)
    encodings_to_try = ['shift-jis', 'utf-8-sig', 'utf-8', 'cp932']

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            
            content = None
            for enc in encodings_to_try:
                try:
                    with open(input_path, 'r', encoding=enc) as f:
                        content = f.read()
                    break
                except:
                    continue
            
            if content is None:
                print(f"Read Error: {filename}")
                continue

            try:
                new_content = modify_sql_content(content, fields, comment_temp)
                # 以 Shift-JIS 保存结果 (Save as Shift-JIS)
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                print(f"Done: {filename}")
            except Exception as e:
                print(f"Process Error {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 插入的字段列表
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 文言模板 (脚本会自动将 ZABWL201R207 替换为真实表名)
comment_template = '''COMMENT ON COLUMN ZABWL201R207.KYOTSU_TSUSU_RENBAN IS '業務共通_通数連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_PAGE_RENBAN IS '業務共通_頁連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_DOFU_RENBAN IS '業務共通_同封内連番'
/'''

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, comment_template)
        print("\nFinished!")
    else:
        print(f"Directory missing: {input_dir}")
