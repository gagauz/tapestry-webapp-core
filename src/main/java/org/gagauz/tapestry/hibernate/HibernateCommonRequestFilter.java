package org.gagauz.tapestry.hibernate;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;

public class HibernateCommonRequestFilter implements RequestFilter {

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
            } catch (HibernateException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error("Failed to close session", e);
            } catch (AssertionFailure af) {
                if (transaction != null) {
                    transaction.rollback();
                }
                af.printStackTrace();
            }
        } else {
            logger.warn("session: {} is already closed!", session);
        }
    }

    @Override
    public boolean service(Request request, Response response, RequestHandler handler) throws IOException {
        if (request.getPath().startsWith("/assets/") || request.getPath().startsWith("/modules/")) {
            return handler.service(request, response);
        }

        boolean commit = true;
        boolean insideTransaction = openSession();
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        System.out.println((request.isXHR() ? "AJAX: " : "") + request.getPath());
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        try {
            return handler.service(request, response);
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
}
