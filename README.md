from pdf2image import convert_from_path
from PIL import Image

def pdf_to_multipage_tiff(pdf_path, output_path):
    print("正在读取 PDF，这可能需要一点时间...")
    
    # 1. 将 PDF 的每一页转为图像对象列表
    # 如果 Windows 用户没有配置 PATH，可以在这里指定 poppler_path=r"C:\你的路径\bin"
    try:
        images = convert_from_path(pdf_path)
    except Exception as e:
        print(f"错误：请确保已安装 Poppler 工具。\n详细错误: {e}")
        return

    print(f"共转换了 {len(images)} 页，正在合并为 TIFF...")

    # 2. 保存为多页 TIFF
    # save_all=True: 保存所有帧
    # append_images: 将剩余的图片追加到第一张后面
    # compression: 使用压缩格式，否则 TIFF 文件会极其巨大
    images[0].save(
        output_path,
        save_all=True,
        append_images=images[1:],
        compression="tiff_deflate" 
    )
    
    print(f"成功！文件已保存为: {output_path}")

# 使用示例
# 请确保 'merged_result.pdf' 是你刚才合成的文件名
pdf_to_multipage_tiff("merged_result.pdf", "output_file.tif")
