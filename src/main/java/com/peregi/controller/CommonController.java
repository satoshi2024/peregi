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
import java.util.UUID;

/**ファイルアップロードとダウンロード
 * @Author:Chikai_Cho
 * @Date 2024/03/21 14:17
 * @Version 1.0
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;

    /**
     * ファイルアップロード
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //ファイルは、指定された場所にダンプされる必要がある一時ファイルである。そうでなければ、この要求が完了した後、一時ファイルは削除される
        log.info(file.toString());

        //オリジナルファイル名
        String originalFilename = file.getOriginalFilename();//abc.jpg
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        //ファイル名の重複によるファイルの上書きを防ぐため、UUIDを使用してファイル名を再生成する
        String fileName = UUID.randomUUID().toString() + suffix;//dfsdfdfd.jpg

        //カタログオブジェクトの作成
        File dir = new File(basePath);
        //現在のディレクトリが存在するかどうかを判断する
        if(!dir.exists()){
            //ディレクトリが存在しない
            dir.mkdirs();
        }

        try {
            //指定した場所に一時ファイルをダンプする
            file.transferTo(new File(basePath + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return R.success(fileName);
    }

    /**
     * ファイルダウンロード
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){

        try {
            //入力ストリーム、入力ストリームを通してファイルの内容を読む
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            //出力ストリームを通して、ファイルをブラウザに書き戻す
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("image/jpeg");

            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1){
                outputStream.write(bytes,0,len);
                outputStream.flush();
            }

            //資源を閉じる
            outputStream.close();
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
