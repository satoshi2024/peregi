# -*- coding: utf-8 -*-
import os
import re

def add_columns_to_content(content, new_fields):
    # 匹配 create table 表名(
    pattern = r"(create\s+table\s+\w+\s*\()"
    replacement = r"\1" + new_fields + ", "
    return re.sub(pattern, replacement, content, flags=re.IGNORECASE)

def batch_transform(input_folder, output_folder, new_fields):
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    for filename in os.listdir(input_folder):
        if filename.lower().endswith((".sql", ".txt")):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)
            try:
                # 1. 按照你要求的 UTF-8 格式读取原始文件
                with open(input_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                modified_content = add_columns_to_content(content, new_fields)

                # 2. 将结果以 Shift-JIS 格式写入新文件
                # errors='replace' 可以防止遇到某些 JIS 不支持的特殊字符时程序崩溃
                with open(output_path, 'w', encoding='shift-jis', errors='replace') as f:
                    f.write(modified_content)
                
                print(f"Success (UTF8 -> SJIS): {filename}")
            except Exception as e:
                print(f"Error {filename}: {e}")

# --- 配置区域 ---
input_dir = './raw_sql' 
output_dir = './processed_sql'

# 使用三引号包裹你长长的字段列表
fields_to_insert = '''KYOTSU_TSUSU_RENBAN                NVARCHAR2(1000),
    KYOTSU_PAGE_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_RENBAN                NVARCHAR2(1000),
    KYOTSU_DOFU_MAISU                 NVARCHAR2(1000),
    KYOTSU_YUBIN_K_KBN                NVARCHAR2(1000),
    KYOTSU_YUHIN_KBN                  NVARCHAR2(1000),
    KYOTSU_CHOSHU_KBN                 NVARCHAR2(1000),
    KYOTSU_GAKKO_KBN                  NVARCHAR2(1000),
    KYOTSU_YOBI_AREA1                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA2                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA3                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA4                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA5                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA6                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA7                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA8                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA9                 NVARCHAR2(1000),
    KYOTSU_YOBI_AREA10                NVARCHAR2(1000),
    KYOTSU_YOBI_AREA11                NVARCHAR2(1000),
    KYOTSU_YOBI_AREA12                NVARCHAR2(1000)'''

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert)
        print("\n转换完成！请在 processed_sql 文件夹查看结果。")
    else:
        print(f"找不到文件夹: {input_dir}")
