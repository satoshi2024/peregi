package com.peregi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author:Chikai_Cho
 * @Date 2024/03/16 19:29
 * @Version 1.0
 */
@Slf4j
@SpringBootApplication
public class PeregiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeregiApplication.class,args);
        log.info("実行を開始します");
    }
}
