# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_template):
    # --- 步骤 1: 动态抓取表名 ---
    table_match = re.search(r"create\s+table\s+(\w+)", content, flags=re.IGNORECASE)
    if table_match:
        real_table_name = table_match.group(1)
        print(f" -> 识别到表名: {real_table_name}")
    else:
        real_table_name = "UNKNOWN_TABLE"
        print(" -> [错误] 未能在文件中识别到 CREATE TABLE 表名")

    # --- 步骤 2: 将文言模板中的占位符替换为当前文件的真实表名 ---
    current_comment_msg = comment_template.replace("ZABWL201R207", real_table_name)

    # --- 步骤 3: 插入新字段 (CREATE TABLE 括号处) ---
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
            content = before_slash + "\n" + current_comment_msg + after_slash
            print(f" -> [成功] 已应用动态表名到 COMMENT")
        else:
            print(" -> [失败] 找到了 COMMENT 关键词但没找到后面的斜杠 /")
    else:
        print(" -> [跳过] 该文件中未发现 COMMENT ON TABLE 语句")
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_temp):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # 定义尝试读取的编码列表
    encodings_to_try = ['shift-jis', 'utf-8-sig', 'utf-8', 'cp932']

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            
            content = None
            # 自动尝试不同编码读取文件
            for enc in encodings_to_try:
                try:
                    with open(input_path, 'r', encoding=enc) as f:
                        content = f.read()
                    print(f"✅ 成功使用 {enc} 读取: {filename}")
                    break 
                except (UnicodeDecodeError, UnicodeError):
                    continue
            
            if content is None:
                print(f"❌ 无法读取文件 {filename}，尝试的所有编码都失败了。")
                continue

            try:
                # 执行修改逻辑
                new_content = modify_sql_content(content, fields, comment_temp)

                # 统一以 shift-jis 格式写出（符合日文系统要求）
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                
            except Exception as e:
                print(f"❌ 处理内容时出错 {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 字段列表
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 文言模板 (脚本会自动替换 ZABWL201R207)
comment_template = '''COMMENT ON COLUMN ZABWL201R207.KYOTSU_TSUSU_RENBAN IS '業務共通_通数連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_PAGE_RENBAN IS '業務共通_頁連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_DOFU_RENBAN IS '業務共通_同封内連番'
/'''

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, comment_template)
        print("\n--- 全部处理程序运行结束 ---")
    else:
        print(f"找不到文件夹: {input_dir}")
