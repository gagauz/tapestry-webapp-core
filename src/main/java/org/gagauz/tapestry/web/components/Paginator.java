package org.gagauz.tapestry.web.components;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Loop;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.gagauz.util.HttpUtils;

/**
 * Paginator component
 */
public class Paginator<T> {

    public static final String FROM_KEY = "p:page-";
    public static final String PAGE_PARAM = "page";
    public static final String XHR_FROM_KEY = ":ipplink/";

    /*
     * Parameters
     */

    @Parameter
    private Zone zone;

    @Parameter
    private String url;

    @Parameter(required = true)
    private List<T> source;

    @Parameter
    private List<T> destination;

    @Parameter(defaultPrefix = "prop")
    private int itemsPerPage;
    private int ipp;

    /*
     * Components
     */
    @Component(parameters = {"source=links", "value=link"})
    private Loop<Integer> linksLoop;

    /*
     * Properties
     */
    @Property
    private int link;

    @Property
    private List<Integer> links;

    @Property
    private int totalPages;

    private int fromPage;

    private int totalCount;

    private boolean init;

    /*
     * Services
     */
    @Inject
    private HttpServletRequest request0;

    @Inject
    private ComponentResources resources;

    public boolean isActive() {
        return fromPage == link;
    }

    public boolean isFirst() {
        return (fromPage <= 1) || (totalPages < 4);
    }

    public boolean isPostFirst() {
        return !links.isEmpty() && links.get(0) < 2;
    }

    public boolean isLast() {
        return (fromPage >= totalPages - 2) || (totalPages < 4);
    }

    public boolean isPreLast() {
        return (fromPage >= totalPages - 3) || (totalPages <= 4);
    }

    public boolean isFirstNear() {
        return (fromPage <= 0);
    }

    public boolean isLastNear() {
        return (fromPage >= totalPages - 1);
    }

    public String getFirstLinkUrl() {
        return attachRequestParameters(0);
    }

    public String getLastLinkUrl() {
        return attachRequestParameters(totalPages);
    }

    public int getPageCount() {
        return totalPages;
    }

    public String getPrevLinkUrl() {
        if (fromPage == 1) {
            return attachRequestParameters();
        } else {
            return attachRequestParameters(fromPage);
        }
    }

    public String getNextLinkUrl() {
        return attachRequestParameters(fromPage + 2);
    }

    public int getNextLinkStr() {
        return fromPage + 2;
    }

    public String getLinkUrl() {
        if (link == 0) {
            return attachRequestParameters();
        }

        return attachRequestParameters(link + 1);
    }

    public int getLinkStr() {
        return link + 1;
    }

    public List<T> getPageItems() {
        if (!init) {
            init(null);
        }

        if (source != null) {
            int to = (fromPage + 1) * ipp >= totalCount ? totalCount : (fromPage + 1) * ipp;
            return source.subList(fromPage * ipp, to);
        }
        return null;
    }

    /*
     * Implementation
     */
    boolean setupRender() {
        if (null == source || source.isEmpty()) {
            return false;
        }

        init(null);

        if (resources.isBound("destination")) {
            destination = getPageItems();
        }

        return links.size() > 1;
    }

    private void init(String pageIndex) {

        if (init) {
            return;
        }

        totalCount = source.size();

        ipp = (itemsPerPage <= 0) ? totalCount : itemsPerPage;

        if (ipp == 0) {
            ipp = 1;
        }

        if (null == pageIndex) {
            pageIndex = HttpUtils.parseQuery(request0.getQueryString()).get(PAGE_PARAM);
        }
        fromPage = NumberUtils.toInt(pageIndex) - 1;
        if (fromPage < 0) {
            fromPage = 0;
        }

        totalPages = totalCount / ipp;

        links = new ArrayList<Integer>(totalPages);

        // Add 1 more page if rest items
        if (totalPages * ipp < totalCount) {
            totalPages++;
        }

        // Check if from index less than total count
        if ((fromPage > totalPages - 1) && (totalPages != 0)) {
            // ESHOP-4053
            // выбрать последнюю страницу, а потом включить фильтр - страниц
            // станет меньше, тогда прыгаем на последнюю
            // throw new InvalidUrlParameterException("Invalid page [" + url +
            // "]");
            fromPage = totalPages - 1;
        }

        int a = Math.max(0, fromPage - 1);
        int b = Math.min(totalPages, fromPage + 2);

        if (fromPage == totalPages - 1) {
            a = Math.max(0, totalPages - 3);
        }
        if (fromPage == 0) {
            b = Math.min(totalPages, 3);
        }

        for (int i = a; i < b; i++) {
            links.add(i);
        }

        init = true;
    }

    private String attachRequestParameters(int... page) {
        String url0 = url;
        if (page.length > 0 && page[0] > 0) {
            if (url0.contains("?")) {
                url0 += '&';
            } else {
                url0 += '?';
            }
            url0 += PAGE_PARAM + '=' + page[0];
        }
        return url0;
    }

    Object onIppLink(String page) {
        init(page);
        return zone != null ? zone.getBody() : null;
    }

    public String getZoneId() {
        return zone != null ? zone.getClientId() : null;
    }
}
