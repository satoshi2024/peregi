# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_template):
    # --- 步骤 1: 提取真实的表名 ---
    # 匹配 create table 后面跟着的名字
    table_match = re.search(r"create\s+table\s+(\w+)", content, flags=re.IGNORECASE)
    if table_match:
        real_table_name = table_match.group(1)
        print(f" -> 检测到表名: {real_table_name}")
    else:
        real_table_name = "UNKNOWN_TABLE"
        print(" -> [警告] 未能提取到表名")

    # --- 步骤 2: 动态生成该文件的 COMMENT 文言 ---
    # 将模板中的 ZABWL201R207 替换为当前文件真实的表名
    current_comment_msg = comment_template.replace("ZABWL201R207", real_table_name)

    # --- 步骤 3: 在 CREATE TABLE 之后插入字段 ---
    pattern_create = r"(create\s+table\s+\w+\s*\()"
    replacement_create = r"\1" + fields_to_add + ", "
    content = re.sub(pattern_create, replacement_create, content, flags=re.IGNORECASE)

    # --- 步骤 4: 关键词定位法插入 COMMENT ---
    keyword = "COMMENT ON TABLE"
    start_pos = content.upper().find(keyword)
    
    if start_pos != -1:
        slash_pos = content.find("/", start_pos)
        if slash_pos != -1:
            before_slash = content[:slash_pos + 1]
            after_slash = content[slash_pos + 1:]
            # 插入动态生成的文言
            content = before_slash + "\n" + current_comment_msg + after_slash
            print(f" -> [成功] 已将文言应用到表 {real_table_name}")
        else:
            print(" -> [失败] 没找到 COMMENT 后的斜杠")
    else:
        print(" -> [失败] 未找到 COMMENT ON TABLE 关键词")
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_temp):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            try:
                with open(input_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                new_content = modify_sql_content(content, fields, comment_temp)

                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                
                print(f"✅ 文件处理完成: {filename}")
            except Exception as e:
                print(f"❌ 运行错误 {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 这里的 ZABWL201R207 将被脚本自动替换为每个文件真实的表名
comment_template = '''COMMENT ON COLUMN ZABWL201R207.KYOTSU_TSUSU_RENBAN IS '業務共通_通数連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_PAGE_RENBAN IS '業務共通_頁連番'
/'''

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, comment_template)
    else:
        print(f"找不到文件夹: {input_dir}")
