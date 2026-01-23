# -*- coding: utf-8 -*-
import os

def merge_files_to_jis(input_folder, output_file):
    # 针对日文环境的编码尝试列表
    encodings_to_try = ['shift-jis', 'cp932', 'utf-8-sig', 'utf-8']
    
    # 获取文件夹内所有 .sql 和 .txt 文件并排序
    files = [f for f in os.listdir(input_folder) if f.lower().endswith(('.sql', '.txt'))]
    files.sort()

    print(f"检测到 {len(files)} 个文件，准备开始以 Shift-JIS 格式整合...")

    # 以 shift-jis 编码打开输出文件
    with open(output_file, 'w', encoding='shift-jis', errors='replace') as outfile:
        for filename in files:
            file_path = os.path.join(input_folder, filename)
            content = None
            
            # 尝试读取文件内容
            for enc in encodings_to_try:
                try:
                    with open(file_path, 'r', encoding=enc) as infile:
                        content = infile.read()
                    print(f"✅ 已读取: {filename} ({enc})")
                    break
                except (UnicodeDecodeError, UnicodeError):
                    continue
            
            if content is not None:
                # 写入来源标注
                outfile.write(f"\n\n-- ==========================================\n")
                outfile.write(f"-- SOURCE: {filename}\n")
                outfile.write(f"-- ==========================================\n\n")
                
                # 写入内容到总文件
                outfile.write(content)
                
                # 确保换行
                if not content.endswith('\n'):
                    outfile.write('\n')
            else:
                print(f"❌ 错误：无法读取文件 {filename}")

# --- 配置区域 ---
# 输入文件夹路径（你处理好的那些 JIS 文件所在的目录）
input_dir = './processed_sql' 
# 整合后的总文件名
output_filename = 'total_merged_jis.sql'

if __name__ == "__main__":
    if os.path.exists(input_dir):
        merge_files_to_jis(input_dir, output_filename)
        print(f"\n--- 整合完成！ ---")
        print(f"最终 JIS 文件已生成: {os.path.abspath(output_filename)}")
    else:
        print(f"错误：找不到文件夹 {input_dir}")
