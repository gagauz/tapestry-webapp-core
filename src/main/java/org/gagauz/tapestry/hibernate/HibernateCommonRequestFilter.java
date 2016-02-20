package org.gagauz.tapestry.hibernate;

import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class HibernateCommonRequestFilter implements ComponentRequestFilter {

    @Inject
    private SessionFactory sessionFactory;

    protected Logger logger = LoggerFactory.getLogger(HibernateCommonRequestFilter.class);

    protected SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    protected void closeSession(boolean commit) throws HibernateException {
        SessionFactory sessionFactory = getSessionFactory();
        SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager
                .unbindResource(sessionFactory);
        logger.debug("Closing Hibernate Session");
        closeSession(sessionHolder.getSession(), commit);
    }

    protected Session openSession(SessionFactory sessionFactory) throws HibernateException {
        Session session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.MANUAL);
        session.beginTransaction();
        logger.debug("begin transaction: {} in session: {}", session.getTransaction(), session);
        return session;
    }

    protected boolean openSession() throws HibernateException {
        SessionFactory sessionFactory = getSessionFactory();
        if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
            // Do not modify the Session: just set the participate flag.
            logger.warn("Use existing Hibernate Session");
            return true;
        }
        logger.debug("Opening Hibernate Session");
        Session session = openSession(sessionFactory);
        TransactionSynchronizationManager.bindResource(sessionFactory, new SessionHolder(session));
        return false;
    }

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

    public void handle(ComponentEventRequestParameters parameters1, PageRenderRequestParameters parameters2, ComponentRequestHandler handler)
            throws IOException {
        boolean commit = true;
        boolean insideTransaction = openSession();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if (null != parameters1) {
            System.out.println(parameters1.getContainingPageName() + "." + parameters1.getNestedComponentId() + ":" + parameters1.getEventType());
        } else {
            System.out.println(parameters2.getLogicalPageName());
        }
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        try {
            if (null != parameters1) {
                handler.handleComponentEvent(parameters1);
            } else {
                handler.handlePageRender(parameters2);
            }
        } catch (Exception e) {
            logger.error(
                    "Catched exception during request handling, mark transaction to rollback!", e);
            commit = false;
            throw new IOException(e);
        } finally {
            if (insideTransaction) {
                logger.warn("Session used in OpenSessionInViewFilter for {} still open!");
            } else {
                closeSession(commit);
            }
        }
    }

    @Override
    public void handleComponentEvent(ComponentEventRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
        handle(parameters, null, handler);
    }

    @Override
    public void handlePageRender(PageRenderRequestParameters parameters, ComponentRequestHandler handler) throws IOException {
        handle(null, parameters, handler);
    }
}
