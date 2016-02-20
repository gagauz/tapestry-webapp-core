package org.gagauz.tapestry.hibernate;

import org.apache.tapestry5.services.*;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import java.io.IOException;

public class HibernateCommonRequestFilter extends OpenSessionInViewFilter implements ComponentRequestFilter {

    protected Logger logger = LoggerFactory.getLogger(HibernateCommonRequestFilter.class);

    @Autowired
    private RequestGlobals requestGlobals;

    protected void closeSession(Session session, boolean commit) {
        if (session.isOpen()) {
            if (commit && session.getFlushMode() == FlushMode.MANUAL) {
                session.flush();
            }
            Transaction transaction = null;
            try {
                transaction = session.getTransaction();
                if (transaction.isActive()) {
                    if (commit) {
                        logger.debug("trying to commit transaction: {} in session: {}",
                                transaction, session);
                        transaction.commit();
                    } else {
                        logger.debug("trying to rollback transaction: {} in session: {}",
                                transaction, session);
                        transaction.rollback();
                    }
                } else {
                    logger.warn("session: {} has no active transaction!", session);
                }
                session.close();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Failed to close session", e);
                throw e;
            }
        } else {
            logger.warn("session: {} is already closed!", session);
        }
    }

    public void handle(final ComponentEventRequestParameters parameters1, final PageRenderRequestParameters parameters2, final ComponentRequestHandler handler)
            throws IOException, ServletException {
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (null != parameters1) {
            System.out.println(parameters1.getContainingPageName() + "." + parameters1.getNestedComponentId() + ":" + parameters1.getEventType());
        } else {
            System.out.println(parameters2.getLogicalPageName());
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        super.doFilterInternal(requestGlobals.getHTTPServletRequest(), requestGlobals.getHTTPServletResponse(), new FilterChain() {

            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                if (null != parameters1) {
                    handler.handleComponentEvent(parameters1);
                } else {
                    handler.handlePageRender(parameters2);
                }
            }
        });
    }

    @Override
    public void handleComponentEvent(ComponentEventRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
        try {
            handle(parameters, null, handler);
        } catch (ServletException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void handlePageRender(PageRenderRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
        try {
            handle(null, parameters, handler);
        } catch (ServletException e) {
            throw new IOException(e);
        }
    }
}
