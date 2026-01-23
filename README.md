import os
import re

def add_columns_to_content(content, new_fields):
    """
    执行 SQL 字符串替换逻辑
    """
    # 匹配 create table 表名(，忽略大小写
    pattern = r"(create\s+table\s+\w+\s*\()"
    replacement = r"\1" + new_fields + ", "
    return re.sub(pattern, replacement, content, flags=re.IGNORECASE)

def batch_transform(input_folder, output_folder, new_fields):
    """
    遍历文件夹并处理所有文件
    """
    # 如果输出文件夹不存在则创建
    if not os.path.exists(output_folder):
        os.makedirs(output_folder)

    # 遍历文件夹
    for filename in os.listdir(input_folder):
        # 你可以根据需要筛选文件后缀，比如 .sql 或 .txt
        if filename.endswith(".sql") or filename.endswith(".txt"):
            input_path = os.path.join(input_folder, filename)
            output_path = os.path.join(output_folder, filename)

            try:
                with open(input_path, 'r', encoding='utf-8') as f:
                    content = f.read()

                # 处理内容
                modified_content = add_columns_to_content(content, new_fields)

                with open(output_path, 'w', encoding='utf-8') as f:
                    f.write(modified_content)
                
                print(f"✅ 已处理: {filename}")
            except Exception as e:
                print(f"❌ 处理 {filename} 时出错: {e}")

# --- 配置区域 ---
# 输入文件夹：放你原始 SQL 文件的路径
input_dir = './raw_sql' 
# 输出文件夹：处理后的文件会存在这里
output_dir = './processed_sql'
# 需要插入的内容
fields_to_insert = "on1 number(38,0),dafd number(38,0)"

if __name__ == "__main__":
    # 确保输入目录存在
    if os.path.exists(input_dir):
        batch_transform(input_dir, output_dir, fields_to_insert)
        print("\n所有文件处理完毕！")
    else:
        print(f"错误：找不到文件夹 '{input_dir}'，请先创建它并放入文件。")
