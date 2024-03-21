package com.peregi.common;

/**
 * カスタムビジネス例外クラス
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
