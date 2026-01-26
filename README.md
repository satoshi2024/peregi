import os
from openpyxl import load_workbook
from openpyxl.styles import Font

def process_excel_with_header(ref_path, folder_path):
    # 标准红色的 Font 对象
    red_font = Font(color="FFFF0000")
    
    # 1. 从参考表提取数据
    ref_wb = load_workbook(ref_path)
    ref_ws = ref_wb.active
    
    ref_rows_data = []
    red_status = [] # 记录哪些行是红色的
    
    # 寻找参考表中的 "No." 单元格
    start_row = 1
    for row in ref_ws.iter_rows(min_col=1, max_col=1):
        if row[0].value == "No.":
            start_row = row[0].row + 1
            break

    # 读取 No. 以下的 20 行
    for i in range(start_row, start_row + 20):
        row_cells = ref_ws[i]
        ref_rows_data.append([cell.value for cell in row_cells])
        # 检查 A 列是否为红色
        is_red = False
        if row_cells[0].font and row_cells[0].font.color:
            if row_cells[0].font.color.rgb == "FFFF0000" or row_cells[0].font.color.indexed == 10:
                is_red = True
        red_status.append(is_red)

    # 2. 遍历文件夹处理文件
    for file_name in os.listdir(folder_path):
        if file_name.endswith(".xlsx") and not file_name.startswith("~$"):
            file_path = os.path.join(folder_path, file_name)
            wb = load_workbook(file_path)
            ws = wb.active
            
            # 寻找目标文件中的 "No." 位置
            target_start_row = 1
            for row in ws.iter_rows(min_col=1, max_col=1):
                if row[0].value == "No.":
                    target_start_row = row[0].row + 1
                    break
            
            # 在 No. 下方插入 20 行
            ws.insert_rows(target_start_row, amount=20)
            
            # 写入数据
            for idx, data_row in enumerate(ref_rows_data):
                curr_row = target_start_row + idx
                for col_idx, value in enumerate(data_row, start=1):
                    ws.cell(row=curr_row, column=col_idx, value=value)

            # 3. 重新对 A 列排序并恢复红色样式
            # 从 No. 下方第一行一直到最后一行
            current_no = 1
            for r in range(target_start_row, ws.max_row + 1):
                cell = ws.cell(row=r, column=1)
                cell.value = current_no
                
                # 如果是新插入的前20行，按参考表的颜色设置
                if r < target_start_row + 20:
                    if red_status[r - target_start_row]:
                        cell.font = red_font
                # 如果是原有数据，你可以根据需要决定是否保留原色或设为默认
                
                current_no += 1
            
            wb.save(file_path)
            print(f"Done: {file_name}")

# --- 配置路径 ---
REF_FILE = "ref.xlsx"        # 参考文件
DIR_PATH = "./my_folders"    # 目标文件夹
process_excel_with_header(REF_FILE, DIR_PATH)
