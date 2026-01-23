# -*- coding: utf-8 -*-
import os

def merge_sql_files(input_folder, output_file):
    # 自动尝试的编码列表
    encodings_to_try = ['shift-jis', 'utf-8-sig', 'utf-8', 'cp932']
    
    # 获取文件夹内所有符合条件的文件
    files = [f for f in os.listdir(input_folder) if f.lower().endswith(('.sql', '.txt'))]
    # 按文件名排序，确保整合顺序有规律
    files.sort()

    print(f"找到 {len(files)} 个文件，准备开始整合...")

    with open(output_file, 'w', encoding='utf-8') as outfile:
        for filename in files:
            file_path = os.path.join(input_folder, filename)
            content = None
            
            # 尝试不同编码读取
            for enc in encodings_to_try:
                try:
                    with open(file_path, 'r', encoding=enc) as infile:
                        content = infile.read()
                    break
                except (UnicodeDecodeError, UnicodeError):
                    continue
            
            if content is not None:
                # 在每个文件内容前加入注释，方便区分来源
                outfile.write(f"\n\n-- ==========================================\n")
                outfile.write(f"-- SOURCE FILE: {filename}\n")
                outfile.write(f"-- ==========================================\n\n")
                
                # 写入内容
                outfile.write(content)
                # 确保每个文件结束后都有换行，防止下一笔内容连在一起
                if not content.endswith('\n'):
                    outfile.write('\n')
                
                print(f"✅ 已整合: {filename}")
            else:
                print(f"❌ 无法读取文件（编码错误）: {filename}")

# --- 配置区域 ---
# 输入文件夹：放你那些分散的 SQL 文件
input_dir = './raw_sql' 
# 输出文件：整合后的文件名
output_filename = 'all_merged_tables.sql'

if __name__ == "__main__":
    if os.path.exists(input_dir):
        merge_sql_files(input_dir, output_filename)
        print(f"\n--- 整合完成！ ---")
        print(f"最终结果已保存至: {os.path.abspath(output_filename)}")
    else:
        print(f"错误：找不到文件夹 {input_dir}")
