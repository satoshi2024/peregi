package com.peregi.controller;

import com.peregi.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @Author:Chikai_Cho
 * @Date 2024/03/26 21:18
 * @Version 1.0
 */
@RestController
@RequestMapping("/common1")
@Slf4j
public class test {
    @Value("${reggie.path}")
    private String basePath;
    @PostMapping("/upload")
    public R<List<String>> upload(@RequestParam("files") MultipartFile[] files){
        // 存储上传成功的文件名列表
        List<String> fileNames = new ArrayList<>();

        // 遍历每个文件
        for (MultipartFile file : files) {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //根据原始文件名生成新的文件名，这里可以使用UUID保证文件名的唯一性
            String fileName = UUID.randomUUID().toString() + originalFilename.substring(originalFilename.lastIndexOf("."));
            try {
                // 将文件保存到指定位置
                file.transferTo(new File(basePath + fileName));
                // 将文件名添加到列表中
                fileNames.add(fileName);
            } catch (IOException e) {
                // 处理文件保存失败的情况
                log.error("文件保存失败：{}", originalFilename);
                e.printStackTrace();
            }
        }

        // 返回上传成功的文件名列表
        return R.success(fileNames);
    }

    /**
     * ファイルダウンロード
     * @param names 文件名列表
     * @param response
     */
    @GetMapping("/download")
    public void download(@RequestParam("names") List<String> names, HttpServletResponse response){
        try {
            // 设置响应内容类型为图片
            response.setContentType("image/jpeg");

            // 获取输出流
            ServletOutputStream outputStream = response.getOutputStream();

            // 遍历文件名列表，逐个发送文件内容到输出流
            for (String name : names) {
                // 读取文件内容
                FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fileInputStream.read(buffer)) != -1) {
                    // 将文件内容写入输出流
                    outputStream.write(buffer, 0, len);
                }
                // 关闭文件输入流
                fileInputStream.close();
            }

            // 刷新输出流
            outputStream.flush();
            // 关闭输出流
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
