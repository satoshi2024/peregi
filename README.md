import os
from openpyxl import load_workbook
from openpyxl.styles import Font

def batch_update_excels(ref_path, folder_path):
    # 准备红色字体样式
    red_font = Font(color="FFFF0000")
    
    # 1. 提取 ref.xlsx 第 7 到 26 行的所有数据和 A 列红色状态
    ref_wb = load_workbook(ref_path, data_only=True) # data_only确保读取的是值而不是公式
    ref_ws = ref_wb.active
    
    extracted_rows = [] # 存储每一行的所有单元格数据
    red_status = []     # 存储 A 列是否为红色
    
    for r in range(7, 27):
        current_row_data = [cell.value for cell in ref_ws[r]]
        extracted_rows.append(current_row_data)
        
        # 检查 A 列的颜色
        a_cell = ref_ws.cell(row=r, column=1)
        is_red = False
        if a_cell.font and a_cell.font.color:
            if a_cell.font.color.rgb == "FFFF0000" or a_cell.font.color.indexed == 10:
                is_red = True
        red_status.append(is_red)
    ref_wb.close()

    # 2. 处理目标文件夹
    for file_name in os.listdir(folder_path):
        if file_name.endswith(".xlsx") and not file_name.startswith("~$"):
            file_path = os.path.join(folder_path, file_name)
            wb = load_workbook(file_path)
            ws = wb.active
            
            # 定位目标文件的 "No." 所在行
            target_no_row = 0
            for row in ws.iter_rows(min_col=1, max_col=1):
                if row[0].value == "No.":
                    target_no_row = row[0].row
                    break
            
            if target_no_row == 0:
                print(f"跳过 {file_name}：未找到 'No.' 标题列")
                continue

            # 在 "No." 行之后插入 20 行空间
            insert_pos = target_no_row + 1
            ws.insert_rows(insert_pos, amount=20)
            
            # 3. 填充整行数据
            for i, row_data in enumerate(extracted_rows):
                curr_row_idx = insert_pos + i
                for col_idx, value in enumerate(row_data, start=1):
                    ws.cell(row=curr_row_idx, column=col_idx, value=value)

            # 4. A 列重新排序：从 1 开始，直到最后一行
            # 并且根据逻辑恢复前 20 行中 A 列的红色
            current_no = 1
            for r in range(insert_pos, ws.max_row + 1):
                a_cell = ws.cell(row=r, column=1)
                a_cell.value = current_no # 强制重排序号
                
                # 处理前 20 行的红色逻辑
                if r < insert_pos + 20:
                    status_idx = r - insert_pos
                    if red_status[status_idx]:
                        a_cell.font = red_font
                    else:
                        a_cell.font = Font(color="00000000") # 设为自动/黑色
                
                current_no += 1
            
            wb.save(file_path)
            print(f"已完成: {file_name}")

# --- 配置区 ---
REFERENCE_FILE = "ref.xlsx"
TARGET_FOLDER = "./data_files" # 请确保这个文件夹路径正确

batch_update_excels(REFERENCE_FILE, TARGET_FOLDER)
