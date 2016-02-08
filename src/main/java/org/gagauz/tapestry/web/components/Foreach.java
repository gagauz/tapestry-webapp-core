package org.gagauz.tapestry.web.components;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.Arrays;
import java.util.Iterator;

@SupportsInformalParameters
public class Foreach<T> {

    @Parameter
    private Iterable<T> source;

    @Parameter(required = true, principal = true)
    private Object value;

    @Parameter(required = false, value = "2147483647", defaultPrefix = BindingConstants.LITERAL)
    private int limit;

    @Parameter(required = false, value = "0", defaultPrefix = BindingConstants.LITERAL)
    private int offset;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String separator;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private Block empty;

    @Parameter
    private int index;

    private Iterator<?> iterator;

    @Inject
    private ComponentResources resources;

    boolean setupRender() {

        if (!resources.isBound("source")) {
            Class<T> valueType = resources.getBoundType("value");

            if (valueType != null && Enum.class.isAssignableFrom(valueType)) {
                source = Arrays.asList(valueType.getEnumConstants());
            }
        }

        if (source == null) {
            return false;
        }

        index = offset;
        iterator = source.iterator();

        for (int i = 0; i < offset && iterator.hasNext(); i++) {
            iterator.next();
        }

        return iterator.hasNext();
    }

    void beginRender(MarkupWriter mw) {
        value = iterator.next();
    }

    boolean afterRender(MarkupWriter mw) {
        ++index;
        boolean continueRender = iterator.hasNext() && (index < limit);
        if (continueRender && separator != null) {
            mw.write(separator);
        }
        return !continueRender;
    }

}
