# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_text_to_add):
    # --- 逻辑 1: CREATE TABLE 插入 (已生效) ---
    pattern_create = r"(create\s+table\s+\w+\s*\()"
    replacement_create = r"\1" + fields_to_add + ", "
    content = re.sub(pattern_create, replacement_create, content, flags=re.IGNORECASE)

    # --- 逻辑 2: 在 COMMENT ON TABLE ... / 下方添加 ---
    # 这个正则会匹配: 
    # 1. COMMENT ON TABLE 表名 IS '';
    # 2. 接着匹配中间的换行和空格 (\s+)
    # 3. 最后匹配斜杠 (/)
    pattern_comment = r"(COMMENT\s+ON\s+TABLE\s+\w+\s+IS\s+'';\s*\n\s*/)"
    
    # 检查是否能匹配到
    match = re.search(pattern_comment, content, flags=re.IGNORECASE)
    
    if match:
        # 在匹配到的内容（包含斜杠）后面加上换行和新文言
        content = re.sub(pattern_comment, r"\1\n" + comment_text_to_add, content, count=1, flags=re.IGNORECASE)
        print(" -> [OK] 成功匹配到 COMMENT 并在其下方插入了内容")
    else:
        # 如果还是不行，打印出文件前 500 个字符帮你分析格式
        print(" -> [ERR] 匹配失败，请检查脚本中的引号或换行符是否与文件一致")
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_msg):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            try:
                # 原始文件是 UTF-8
                with open(input_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                new_content = modify_sql_content(content, fields, comment_msg)

                # 输出为 Shift-JIS
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                
                print(f"✅ 处理文件: {filename}")
            except Exception as e:
                print(f"❌ 错误 {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 1. 插入字段
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 2. 插入文言 (确保使用三引号，且末尾也有斜杠)
new_comment_msg = '''COMMENT ON COLUMN ZABWL201R207.KYOTSU_TSUSU_RENBAN IS '業務共通_通数連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_PAGE_RENBAN IS '業務共通_頁連番'
/'''

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, new_comment_msg)
    else:
        print(f"找不到文件夹: {input_dir}")
