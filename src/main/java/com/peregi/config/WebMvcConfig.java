package com.peregi.config;

import com.peregi.common.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {
    /**
     * 静的リソースのマッピングを設定し
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静的なリソースマッピングを開始します...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * メッセージ・コンバーターによるmvcフレームワークの拡張
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("拡張メッセージコンバータ...");
        //メッセージコンバータオブジェクトの作成
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //Javaオブジェクトをjsonに変換するJacksonの基本的な用途であるオブジェクト・コンバータを設定
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //上記のメッセージ・コンバーター・オブジェクトをmvcフレームワークのコンバーター・コレクションに追加
        converters.add(0,messageConverter);
    }
}
