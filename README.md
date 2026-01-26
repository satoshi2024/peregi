import os
from openpyxl import load_workbook
from openpyxl.styles import Font
from copy import copy

def batch_update_with_logic(ref_path, folder_path):
    # 1. 提取参考数据及完整样式 (ref.xlsx A7:A26)
    ref_wb = load_workbook(ref_path, data_only=True)
    ref_ws = ref_wb.active
    
    ref_rows_template = [] 
    for r in range(7, 27):
        ref_rows_template.append(list(ref_ws[r]))
    ref_wb.close()

    # 2. 获取文件夹内所有 xlsx 文件（兼容大小写）
    files = [f for f in os.listdir(folder_path) if f.lower().endswith(".xlsx") and not f.startswith("~$")]

    for file_name in files:
        file_path = os.path.join(folder_path, file_name)
        print(f"正在处理: {file_name} ...", end="")
        
        wb = load_workbook(file_path)
        ws = wb.active
        
        # 定位目标文件的 "No." 所在行
        target_no_row = 0
        for row in ws.iter_rows(min_col=1, max_col=1):
            if str(row[0].value).strip() == "No.":
                target_no_row = row[0].row
                break
        
        if target_no_row == 0:
            print(" [跳过: 未找到 No.]")
            continue

        # 在 "No." 下方插入 20 行
        insert_pos = target_no_row + 1
        ws.insert_rows(insert_pos, amount=20)
        
        # 3. 填充数据并克隆样式 (确保格式一致)
        for i, source_row in enumerate(ref_rows_template):
            target_row_idx = insert_pos + i
            for col_idx, source_cell in enumerate(source_row, start=1):
                target_cell = ws.cell(row=target_row_idx, column=col_idx)
                target_cell.value = source_cell.value
                
                # 复制所有格式：字体、边框、填充、对齐等
                if source_cell.has_style:
                    target_cell.font = copy(source_cell.font)
                    target_cell.border = copy(source_cell.border)
                    target_cell.fill = copy(source_cell.fill)
                    target_cell.number_format = copy(source_cell.number_format)
                    target_cell.protection = copy(source_cell.protection)
                    target_cell.alignment = copy(source_cell.alignment)

        # 4. A 列动态重排序逻辑
        # 要求：B 列有值才排序；B 列一旦没值，停止 No. 排序
        current_no = 1
        for r in range(insert_pos, ws.max_row + 1):
            b_cell_value = ws.cell(row=r, column=2).value # 获取 B 列的值
            a_cell = ws.cell(row=r, column=1)
            
            if b_cell_value is not None and str(b_cell_value).strip() != "":
                # B 列有值，进行排序
                a_cell.value = current_no
                current_no += 1
            else:
                # B 列没值，清空该行的 No. 并停止后续排序（跳出循环）
                a_cell.value = None
                # 如果你希望仅仅是“这一行不参与”但后面还要继续，就把 break 改成 continue
                # 根据你的描述“停止排序”，这里使用 break
                break
            
        wb.save(file_path)
        print(" [成功]")

# --- 配置区 ---
REFERENCE_FILE = "ref.xlsx"
TARGET_FOLDER = "." # 处理当前目录下所有 xlsx

if __name__ == "__main__":
    batch_update_with_logic(REFERENCE_FILE, TARGET_FOLDER)
