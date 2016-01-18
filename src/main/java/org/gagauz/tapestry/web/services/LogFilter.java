package org.gagauz.tapestry.web.services;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;

public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("=================================================================================");
        System.out.println(((HttpServletRequest) request).getRequestURI());
        //chain.doFilter(request, response);
        System.out.println("=================================================================================");
    }

    @Override
    public void destroy() {

    }

}
