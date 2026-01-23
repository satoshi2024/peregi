# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_template):
    # --- 步骤 1: 动态抓取表名 ---
    # 查找 create table 后面紧跟的单词
    table_match = re.search(r"create\s+table\s+(\w+)", content, flags=re.IGNORECASE)
    if table_match:
        real_table_name = table_match.group(1)
        print(f" -> 识别到表名: {real_table_name}")
    else:
        real_table_name = "UNKNOWN_TABLE"
        print(" -> [错误] 未能识别到表名")

    # --- 步骤 2: 将文言模板中的占位符替换为真实表名 ---
    # 这样每个文件的 COMMENT 都会指向它自己的表名
    current_comment_msg = comment_template.replace("ZABWL201R207", real_table_name)

    # --- 步骤 3: 插入新字段 (CREATE TABLE 处) ---
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
            
            # 在第一个斜杠后面插入换行和该表专用的文言
            content = before_slash + "\n" + current_comment_msg + after_slash
            print(f" -> [成功] 已应用动态表名到 COMMENT")
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
                # 【关键修正】针对你的报错，读取编码改为 shift-jis
                with open(input_path, 'r', encoding='shift-jis', errors='ignore') as f:
                    content = f.read()

                new_content = modify_sql_content(content, fields, comment_temp)

                # 写入编码依然使用 shift-jis (保持日文系统环境一致)
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                
                print(f"✅ 处理完毕: {filename}")
            except Exception as e:
                print(f"❌ 运行报错 {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 字段列表
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 文言模板 (脚本会自动把这里的 ZABWL201R207 换成真实的表名)
comment_template = '''COMMENT ON COLUMN ZABWL201R207.KYOTSU_TSUSU_RENBAN IS '業務共通_通数連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_PAGE_RENBAN IS '業務共通_頁連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_DOFU_RENBAN IS '業務共通_同封内連番'
/'''

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, comment_template)
    else:
        print(f"找不到文件夹: {input_dir}")
