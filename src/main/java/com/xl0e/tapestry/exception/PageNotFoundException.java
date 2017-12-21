package com.xl0e.tapestry.exception;

import org.apache.tapestry5.web.pages.Error404;

public class PageNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -5196539392925238287L;

    private String pageToRender = Error404.class.getSimpleName();

    public static PageNotFoundException renderPage(final String pageToRender) {
        PageNotFoundException e = new PageNotFoundException();
        e.pageToRender = pageToRender;
        return e;
    }

    public String getPageToRender() {
        return pageToRender;
    }
}
