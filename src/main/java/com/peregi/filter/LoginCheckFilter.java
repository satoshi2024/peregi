package com.peregi.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.AntPathMatcher;

import com.alibaba.fastjson.JSON;
import com.peregi.common.R;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户のログインが完了しているかどうかを確認し
 */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter{
    //パスマッチャー、ワイルドカードをサポートしています
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、現在のリクエストのURIを取得します
        String requestURI = request.getRequestURI();// /backend/index.html

        log.info("リクエストをインターセプトしました：{}",requestURI);

        //処理する必要のないリクエストパスを定義します
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**"
        };


        //2、現在のリクエストを処理する必要があるかどうかを判断します
        boolean check = check(urls, requestURI);

        //3、処理の必要がない場合は、直接通過させます
        if(check){
            log.info("このリクエスト{}は処理の必要がない",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4、ログイン状態を判断し、ログインしている場合は直接通過させます
        if(request.getSession().getAttribute("employee") != null){
            log.info("ユーザーがログインし,ユーザーIDは：{}",request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        log.info("ユーザーはログインしていません");
        //5、ログインしないの場合、未ログインの結果を返します。データをクライアントページに出力ストリーム方式で応答し
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * パスの一致を確認し、現在のリクエストを放行する必要があるかどうかをチェックし
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match){
                return true;
            }
        }
        return false;
    }
}
