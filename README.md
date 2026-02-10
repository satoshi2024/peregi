
from pypdf import PdfWriter
import os

def merge_pdfs(output_filename, pdf_list):
    merger = PdfWriter()

    for pdf in pdf_list:
        merger.append(pdf)

    merger.write(output_filename)
    merger.close()
    print(f"合并完成！文件已保存为: {output_filename}")

# 在这里列出你想合并的文件名，按顺序排列
pdfs_to_merge = ["file1.pdf", "file2.pdf", "file3.pdf"]

merge_pdfs("merged_result.pdf", pdfs_to_merge)

