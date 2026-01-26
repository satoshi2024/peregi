import os
from openpyxl import load_workbook

def compare_folders_ignore_extension_case(folder_a, folder_b, log_file="diff_log.txt"):
    """
    对比两个文件夹下的 Excel 文件，忽略 .xlsx 和 .XLSX 的后缀差异。
    """
    
    # 辅助函数：获取目录下所有 excel 文件，返回 {小写主文件名: 原始完整文件名}
    def get_excel_map(folder):
        excel_map = {}
        for f in os.listdir(folder):
            if f.lower().endswith(".xlsx") and not f.startswith("~$"):
                # 获取主文件名（不含点号后的部分）
                main_name = os.path.splitext(f)[0].lower()
                excel_map[main_name] = f
        return excel_map

    map_a = get_excel_map(folder_a)
    map_b = get_excel_map(folder_b)

    # 逻辑匹配
    main_names_a = set(map_a.keys())
    main_names_b = set(map_b.keys())
    
    common_main_names = main_names_a.intersection(main_names_b)
    only_in_a = main_names_a - main_names_b
    only_in_b = main_names_b - main_names_a

    with open(log_file, "w", encoding="utf-8") as log:
        log.write("=== Excel 差异对比报告 (忽略后缀大小写) ===\n")
        log.write(f"文件夹 A: {folder_a}\n")
        log.write(f"文件夹 B: {folder_b}\n\n")

        if only_in_a:
            log.write(f"[缺失] 仅在 A 中存在: {[map_a[m] for m in only_in_a]}\n")
        if only_in_b:
            log.write(f"[缺失] 仅在 B 中存在: {[map_b[m] for m in only_in_b]}\n")
        log.write("-" * 60 + "\n\n")

        for main_name in sorted(common_main_names):
            name_a = map_a[main_name]
            name_b = map_b[main_name]
            log.write(f"正在对比: {name_a} <-> {name_b}\n")
            
            path_a = os.path.join(folder_a, name_a)
            path_b = os.path.join(folder_b, name_b)

            try:
                # 使用 data_only=True 对比数值
                wb_a = load_workbook(path_a, data_only=True)
                wb_b = load_workbook(path_b, data_only=True)

                for sheet_name in wb_a.sheetnames:
                    if sheet_name not in wb_b.sheetnames:
                        log.write(f"  [跳过] Sheet [{sheet_name}] 不在 B 文件中\n")
                        continue
                    
                    ws_a = wb_a[sheet_name]
                    ws_b = wb_b[sheet_name]

                    # 记录该 Sheet 是否有差异
                    found_diff_in_sheet = False
                    
                    # 对比范围取两表最大并集
                    max_r = max(ws_a.max_row, ws_b.max_row)
                    max_c = max(ws_a.max_column, ws_b.max_column)

                    for r in range(1, max_r + 1):
                        for c in range(1, max_c + 1):
                            val_a = ws_a.cell(row=r, column=c).value
                            val_b = ws_b.cell(row=r, column=c).value

                            if val_a != val_b:
                                coord = ws_a.cell(row=r, column=c).coordinate
                                log.write(f"  [差异] 位置:{coord} | A:({val_a}) | B:({val_b})\n")
                                found_diff_in_sheet = True
                    
                    if not found_diff_in_sheet:
                        log.write(f"  [正常] Sheet [{sheet_name}] 内容一致\n")

                wb_a.close()
                wb_b.close()
            except Exception as e:
                log.write(f"  [读取失败]: {e}\n")
            log.write("\n")

    print(f"对比任务结束！差异日志见: {log_file}")

# --- 修改此处路径 ---
FOLDER_1 = "./origin"
FOLDER_2 = "./processed"

if __name__ == "__main__":
    compare_folders_ignore_extension_case(FOLDER_1, FOLDER_2)
