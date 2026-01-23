# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_template):
    # --- 1. 自动提取真实的表名 ---
    # 匹配 create table 后面跟着的名字（支持带引号或不带引号）
    table_match = re.search(r"create\s+table\s+([\w\d\"]+)", content, flags=re.IGNORECASE)
    if table_match:
        real_table_name = table_match.group(1).replace('"', '') # 移除可能的引号
        print(f" -> 识别到表名: {real_table_name}")
    else:
        real_table_name = "UNKNOWN_TABLE"
        print(" -> [警告] 未能提取到表名")

    # --- 2. 动态生成文言：将模板中的占位符换成真实表名 ---
    # 脚本会自动把模板里的 ZABWL201R207 换成该文件真实的表名
    current_comment_msg = comment_template.replace("ZABWL201R207", real_table_name)

    # --- 3. 逻辑 A: 在 CREATE TABLE 之后插入新字段 ---
    pattern_create = r"(create\s+table\s+[\w\d\"]+\s*\()"
    replacement_create = r"\1" + fields_to_add + ", "
    content = re.sub(pattern_create, replacement_create, content, flags=re.IGNORECASE)

    # --- 4. 逻辑 B: 关键词定位法插入新文言 ---
    keyword = "COMMENT ON TABLE"
    start_pos = content.upper().find(keyword)
    
    if start_pos != -1:
        # 从关键词位置开始找第一个斜杠 /
        slash_pos = content.find("/", start_pos)
        if slash_pos != -1:
            before_slash = content[:slash_pos + 1]
            after_slash = content[slash_pos + 1:]
            # 插入动态生成的注释内容
            content = before_slash + "\n" + current_comment_msg + after_slash
            print(f" -> [成功] 已应用动态表名 {real_table_name} 到注释区")
        else:
            print(" -> [失败] 找到 COMMENT 但没找到随后的 /")
    else:
        print(" -> [跳过] 此文件无 COMMENT ON TABLE 语句")
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_temp):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            
            content = None
            # --- 自动处理编码报错：先试 shift-jis，再试 utf-8 ---
            for enc in ['shift-jis', 'utf-8-sig', 'utf-8']:
                try:
                    with open(input_path, 'r', encoding=enc) as f:
                        content = f.read()
                    break
                except UnicodeDecodeError:
                    continue
            
            if content is None:
                print(f"❌ 编码错误：无法读取文件 {filename}")
                continue

            try:
                new_content = modify_sql_content(content, fields, comment_temp)

                # 以 JIS (Shift-JIS) 格式保存输出文件
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                print(f"✅ 处理完成: {filename}")
            except Exception as e:
                print(f"❌ 处理出错 {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 1. 要插入的列
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 2. 注释模板 (ZABWL201R207 会被自动替换)
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
        print(f"文件夹不存在: {input_dir}")
