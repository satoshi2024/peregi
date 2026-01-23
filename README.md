# -*- coding: utf-8 -*-
import os

def merge_all_files(input_folder, output_filename):
    # 自动尝试的编码列表，确保能读入各种格式的文件
    encodings_to_try = ['shift-jis', 'utf-8-sig', 'utf-8', 'cp932']
    
    # 获取文件夹内所有的 .sql 和 .txt 文件
    files = [f for f in os.listdir(input_folder) if f.lower().endswith(('.sql', '.txt'))]
    # 按文件名排序，保证整合顺序
    files.sort()

    print(f"检测到 {len(files)} 个文件，准备开始合并...")

    # 使用 utf-8 编码保存最终的总文件
    with open(output_filename, 'w', encoding='utf-8') as outfile:
        for filename in files:
            file_path = os.path.join(input_folder, filename)
            content = None
            
            # 尝试不同编码读取单个文件内容
            for enc in encodings_to_try:
                try:
                    with open(file_path, 'r', encoding=enc) as infile:
                        content = infile.read()
                    print(f"✅ 已读取 (编码 {enc}): {filename}")
                    break
                except (UnicodeDecodeError, UnicodeError):
                    continue
            
            if content is not None:
                # 在合并的内容中加入来源说明，方便后续查看
                outfile.write(f"\n\n-- ==========================================\n")
                outfile.write(f"-- SOURCE: {filename}\n")
                outfile.write(f"-- ==========================================\n\n")
                
                outfile.write(content)
                
                # 确保每个文件的结尾都有换行符，防止内容连在一起
                if not content.endswith('\n'):
                    outfile.write('\n')
            else:
                print(f"❌ 无法读取文件（编码不支持）: {filename}")

# --- 配置区域 ---
# 1. 之前处理好的文件所在的文件夹路径 (例如 processed_sql)
input_dir = './processed_sql' 
# 2. 合并后的总文件名
final_output = 'all_tables_merged.sql'

if __name__ == "__main__":
    if os.path.exists(input_dir):
        merge_all_files(input_dir, final_output)
        print(f"\n--- 整合任务完成！ ---")
        print(f"总文件已生成: {os.path.abspath(final_output)}")
    else:
        print(f"错误：找不到文件夹 {input_dir}")
