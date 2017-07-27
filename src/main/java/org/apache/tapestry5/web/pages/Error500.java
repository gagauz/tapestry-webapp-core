package org.apache.tapestry5.web.pages;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ExceptionReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Error500 implements ExceptionReporter {

    private static final Logger LOG = LoggerFactory.getLogger(Error500.class);

    @Inject
    @Symbol(SymbolConstants.PRODUCTION_MODE)
    private String production;

    @Property
    private String error;

    @Override
    public void reportException(Throwable exception) {
        if ("true".equals(production)) {
            LOG.error("", exception);
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            exception.printStackTrace(pw);
            error = sw.toString();
        }
    }
}
