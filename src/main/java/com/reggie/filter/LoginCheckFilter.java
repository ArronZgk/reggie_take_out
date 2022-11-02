package com.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.reggie.common.BaseContext;
import com.reggie.common.R;
import com.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符，否则/backend/**只能访问路径，不能访问backend下的index.html页面等
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1. 获取本次请求的uri
        String requestUri = request.getRequestURI();

        log.info("拦截到请求：{}", requestUri);

        //定义不需要处理的请求路径，backend和front下的静态资源可以不用处理，
        // /common/**是上传图片的放行页面
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        //2.判断本次请求是否需要处理
        boolean check = check(uris, requestUri);

        //3. 如果不不需要处理，则直接放行
        if (check) {
            log.info("本次请求{}不需要处理", requestUri);
            filterChain.doFilter(request, response);
            return;
        }
        //4-1 判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null) {
            log.info("用户已登录，用户的id{}", (request.getSession().getAttribute("employee")));

            //将用户的id放到线程中记性储存
            BaseContext.setId((Long) request.getSession().getAttribute("employee"));

            filterChain.doFilter(request, response);
            return;
        }

        //4-2 判断登录状态，如果已登录，则直接放行（移动端）
        if (request.getSession().getAttribute("user") != null) {
            log.info("用户已登录，用户的id{}", (request.getSession().getAttribute("user")));

            //将用户的id放到线程中进行储存
            BaseContext.setId((Long) request.getSession().getAttribute("user"));

            filterChain.doFilter(request, response);
            return;
        }

        //5. 如果未登录则返回未登录结果，通过输出流向客户端页面响应数据
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 检测本次请求是否需要放行
     * @param requestUri
     * @return
     */
    public boolean check(String[] uris, String requestUri) {
        for (String uri : uris) {
            boolean match = PATH_MATCHER.match(uri, requestUri);
            if (match) {
                return true;
            }
        }
        return false;
    }

}
