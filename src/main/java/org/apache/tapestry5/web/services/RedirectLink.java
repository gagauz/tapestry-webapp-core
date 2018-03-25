package org.apache.tapestry5.web.services;

public class RedirectLink {
    private final Class<?> pageClass;
    private final String pageName;
    private final Object[] context;

    public static RedirectLink forPage(Class<?> pageClass, Object... context) {
        return new RedirectLink(pageClass, context);
    }

    public static RedirectLink forPage(String pageName, Object... context) {
        return new RedirectLink(pageName, context);
    }

    private RedirectLink(Class<?> pageClass, Object[] context) {
        this.pageClass = pageClass;
        this.pageName = null;
        this.context = context;
    }

    private RedirectLink(String pageName, Object[] context) {
        this.pageClass = null;
        this.pageName = pageName;
        this.context = context;
    }

    public Class<?> getPageClass() {
        return pageClass;
    }

    public String getPageName() {
        return pageName;
    }

    public Object[] getContext() {
        return context;
    }

}
