package com.peregi.filter;

import com.alibaba.fastjson.JSON;
import com.peregi.common.BaseContext;
import com.peregi.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //2、現在のリクエストを処理する必要があるかどうかを判断します
        boolean check = check(urls, requestURI);

        //3、処理の必要がない場合は、直接通過させます
        if(check){
            log.info("このリクエスト{}は処理の必要がない",requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //4-1、ログイン状態を判断し、ログインしている場合は直接通過させます
        if(request.getSession().getAttribute("employee") != null){
            log.info("ユーザーがログインし,ユーザーIDは：{}",request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }

        //4-2、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }

        log.info("ユーザーはログインしていません");
        //5、如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
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
