import os
from openpyxl import load_workbook
from openpyxl.styles import Font, Alignment, Border, Side, PatternFill
from copy import copy

def batch_update_with_styles(ref_path, folder_path):
    # 1. 提取参考数据及完整样式
    ref_wb = load_workbook(ref_path, data_only=True)
    ref_ws = ref_wb.active
    
    # 存储整行单元格对象，以便后续复制样式
    ref_rows_template = [] 
    for r in range(7, 27):
        ref_rows_template.append(list(ref_ws[r]))

    # 2. 处理目标文件夹
    # 增加对 .xlsx 和 .XLSX 的同时支持
    files = [f for f in os.listdir(folder_path) if f.lower().endswith(".xlsx") and not f.startswith("~$")]

    for file_name in files:
        file_path = os.path.join(folder_path, file_name)
        print(f"正在处理样式同步: {file_name}...")
        
        wb = load_workbook(file_path)
        ws = wb.active
        
        # 定位 "No."
        target_no_row = 0
        for row in ws.iter_rows(min_col=1, max_col=1):
            if str(row[0].value).strip() == "No.":
                target_no_row = row[0].row
                break
        
        if target_no_row == 0:
            print(f" [跳过] {file_name} 未找到 'No.'")
            continue

        # 插入 20 行
        insert_pos = target_no_row + 1
        ws.insert_rows(insert_pos, amount=20)
        
        # 3. 填充数据并克隆样式
        for i, source_row in enumerate(ref_rows_template):
            target_row_idx = insert_pos + i
            for col_idx, source_cell in enumerate(source_row, start=1):
                target_cell = ws.cell(row=target_row_idx, column=col_idx)
                
                # 复制值
                target_cell.value = source_cell.value
                
                # 复制样式 (必须使用 copy，否则多个单元格共享同一个样式对象会导致异常)
                if source_cell.has_style:
                    target_cell.font = copy(source_cell.font)
                    target_cell.border = copy(source_cell.border)
                    target_cell.fill = copy(source_cell.fill)
                    target_cell.number_format = copy(source_cell.number_format)
                    target_cell.protection = copy(source_cell.protection)
                    target_cell.alignment = copy(source_cell.alignment)

        # 4. A列全局重排序，同时保持“No.”下方数据的颜色逻辑
        current_no = 1
        for r in range(insert_pos, ws.max_row + 1):
            a_cell = ws.cell(row=r, column=1)
            a_cell.value = current_no
            # 注意：此处不再强制修改 font 颜色，因为上面样式克隆已经把 ref 的颜色带过来了。
            # 只有 20 行以后的数据，如果原本没颜色，会保持原有文件的默认样式。
            current_no += 1
            
        wb.save(file_path)

# --- 配置区 ---
REFERENCE_FILE = "ref.xlsx"
TARGET_FOLDER = "." # 设置为当前目录

batch_update_with_styles(REFERENCE_FILE, TARGET_FOLDER)
