# -*- coding: utf-8 -*-
import os
import re

def add_columns_to_content(content, new_fields):
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
                # 读取时尝试 shift-jis (日文系统常用)
                with open(input_path, 'r', encoding='shift-jis', errors='ignore') as f:
                    content = f.read()

                modified_content = add_columns_to_content(content, new_fields)

                with open(output_path, 'w', encoding='utf-8') as f:
                    f.write(modified_content)
                print(f"Success: {filename}")
            except Exception as e:
                print(f"Error {filename}: {e}")

input_dir = './raw_sql' 
output_dir = './processed_sql'
fields_to_insert = "on1 number(38,0),dafd number(38,0)"

if __name__ == "__main__":
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert)
    else:
        print(f"Directory not found: {input_dir}")
