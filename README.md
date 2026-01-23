# -*- coding: utf-8 -*-
import os
import re

def modify_sql_content(content, fields_to_add, comment_text_to_add):
    # --- 逻辑 1: 在 CREATE TABLE 之后插入字段 (正则法) ---
    pattern_create = r"(create\s+table\s+\w+\s*\()"
    replacement_create = r"\1" + fields_to_add + ", "
    content = re.sub(pattern_create, replacement_create, content, flags=re.IGNORECASE)

    # --- 逻辑 2: 关键词定位法插入 COMMENT ---
    # 1. 找到 "COMMENT ON TABLE" 的起始位置
    keyword = "COMMENT ON TABLE"
    start_pos = content.upper().find(keyword)
    
    if start_pos != -1:
        # 2. 从这个位置开始往后找第一个斜杠 "/"
        slash_pos = content.find("/", start_pos)
        if slash_pos != -1:
            # 3. 在斜杠后面插入新内容 (slash_pos + 1 是斜杠后的位置)
            # 我们先切开字符串，中间塞入新内容，再拼起来
            before_slash = content[:slash_pos + 1]
            after_slash = content[slash_pos + 1:]
            
            content = before_slash + "\n" + comment_text_to_add + after_slash
            print(" -> [成功] 已通过关键词定位并在第一个 / 后插入内容")
        else:
            print(" -> [失败] 找到了 COMMENT ON TABLE 但没找到后面的 /")
    else:
        print(" -> [失败] 未能找到关键词 'COMMENT ON TABLE'")
    
    return content

def batch_transform(input_folder, output_folder, fields, comment_msg):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            try:
                # 按照要求使用 UTF-8 读取
                with open(input_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # 执行修改逻辑
                new_content = modify_sql_content(content, fields, comment_msg)

                # 按照要求使用 Shift-JIS 写入
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(new_content)
                
                print(f"✅ 处理完成: {filename}")
            except Exception as e:
                print(f"❌ 运行错误 {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 字段内容
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000)'''

# 插入的文言内容 (请确保格式正确)
new_comment_msg = '''COMMENT ON COLUMN ZABWL201R207.KYOTSU_TSUSU_RENBAN IS '業務共通_通数連番'
/
COMMENT ON COLUMN ZABWL201R207.KYOTSU_PAGE_RENBAN IS '業務共通_頁連番'
/'''

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert, new_comment_msg)
    else:
        print(f"找不到文件夹: {input_dir}")
