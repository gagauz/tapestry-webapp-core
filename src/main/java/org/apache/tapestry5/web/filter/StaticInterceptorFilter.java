package org.apache.tapestry5.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticInterceptorFilter extends AbstractHttpFilter {

    private RequestDispatcher defaultRequestDispatcher;

    @Override
    public void destroy() {
    }

    @Override
    public void filter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        defaultRequestDispatcher.forward(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.defaultRequestDispatcher = filterConfig.getServletContext().getNamedDispatcher("default");
    }

}
