import os
from openpyxl import load_workbook

def compare_folders(folder_a, folder_b, log_file="diff_log.txt"):
    """
    对比两个文件夹下同名 Excel 文件的内容差异
    """
    # 获取两个文件夹的文件列表
    files_a = set(f for f in os.listdir(folder_a) if f.lower().endswith(".xlsx") and not f.startswith("~$"))
    files_b = set(f for f in os.listdir(folder_b) if f.lower().endswith(".xlsx") and not f.startswith("~$"))
    
    # 找出共同拥有的文件
    common_files = files_a.intersection(files_b)
    only_in_a = files_a - files_b
    only_in_b = files_b - files_a

    with open(log_file, "w", encoding="utf-8") as log:
        log.write("=== Excel 文件夹对比报告 ===\n")
        log.write(f"文件夹 A: {folder_a}\n")
        log.write(f"文件夹 B: {folder_b}\n\n")

        # 记录缺失文件
        if only_in_a:
            log.write(f"[缺失] 以下文件仅存在于文件夹 A: {', '.join(only_in_a)}\n")
        if only_in_b:
            log.write(f"[缺失] 以下文件仅存在于文件夹 B: {', '.join(only_in_b)}\n")
        log.write("-" * 50 + "\n\n")

        # 对比同名文件
        for file_name in sorted(common_files):
            log.write(f"正在对比文件: {file_name}\n")
            path_a = os.path.join(folder_a, file_name)
            path_b = os.path.join(folder_b, file_name)

            try:
                wb_a = load_workbook(path_a, data_only=True)
                wb_b = load_workbook(path_b, data_only=True)
                
                # 对比每个 Sheet
                sheets_a = wb_a.sheetnames
                sheets_b = wb_b.sheetnames

                if sheets_a != sheets_b:
                    log.write(f"  [警告] Sheet 页签名称不一致!\n")
                    log.write(f"    A包含: {sheets_a}\n")
                    log.write(f"    B包含: {sheets_b}\n")

                for sheet_name in sheets_a:
                    if sheet_name not in wb_b.sheetnames:
                        log.write(f"  [跳过] Sheet [{sheet_name}] 仅存在于文件 A 中\n")
                        continue
                    
                    ws_a = wb_a[sheet_name]
                    ws_b = wb_b[sheet_name]

                    # 确定对比的最大范围
                    max_r = max(ws_a.max_row, ws_b.max_row)
                    max_c = max(ws_a.max_column, ws_b.max_column)

                    diff_found = False
                    for r in range(1, max_r + 1):
                        for c in range(1, max_c + 1):
                            val_a = ws_a.cell(row=r, column=c).value
                            val_b = ws_b.cell(row=r, column=c).value

                            if val_a != val_b:
                                diff_found = True
                                cell_ref = ws_a.cell(row=r, column=c).coordinate
                                log.write(f"  [差异] Sheet: {sheet_name} | 单元格: {cell_ref} | A值: '{val_a}' vs B值: '{val_b}'\n")
                    
                    if not diff_found:
                        log.write(f"  [正常] Sheet [{sheet_name}] 内容完全一致\n")

                wb_a.close()
                wb_b.close()

            except Exception as e:
                log.write(f"  [错误] 无法读取文件 {file_name}: {e}\n")
            
            log.write("\n")

    print(f"对比完成！详细差异已保存至: {log_file}")

# --- 配置区 ---
FOLDER_BEFORE = "./old_files"  # 对比文件夹 1
FOLDER_AFTER = "./new_files"   # 对比文件夹 2 (处理后的)

if __name__ == "__main__":
    # 如果文件夹在当前目录下，确保路径正确
    compare_folders(FOLDER_BEFORE, FOLDER_AFTER)
