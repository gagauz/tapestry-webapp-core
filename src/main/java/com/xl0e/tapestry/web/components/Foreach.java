package com.xl0e.tapestry.web.components;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.CleanupRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.annotations.SupportsInformalParameters;
import org.apache.tapestry5.ioc.annotations.Inject;

@SupportsInformalParameters
public class Foreach<T> {

    private static Iterator emptyIterator = new Iterator() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }
    };

    @Parameter
    private Iterable<T> source;

    @Parameter(required = true, principal = true)
    private T value;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String element;

    @Parameter(required = false, defaultPrefix = BindingConstants.LITERAL)
    private Integer limit;

    @Parameter(required = false, value = "0", defaultPrefix = BindingConstants.LITERAL)
    private int offset;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private String separator;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private Block empty;

    @Parameter(defaultPrefix = BindingConstants.LITERAL)
    private Block ellipsis;

    @Parameter
    private int index;

    @Parameter(value = "0")
    private int indexStart;

    private Iterator<T> iterator;

    @Inject
    private ComponentResources resources;

    private Block cleanupBlock;

    private Iterator<T> getIterator() {
        if (null == this.iterator) {
            this.iterator = (null == this.source) ? emptyIterator : this.source.iterator();
        }
        return this.iterator;
    }

    private boolean canAdvance() {
        return (null == this.limit || this.index < this.limit) && getIterator().hasNext();
    }

    @SetupRender
    boolean start() {
        this.iterator = null;
        this.index = indexStart;
        if (!this.resources.isBound("source")) {
            Class<T> valueType = this.resources.getBoundType("value");

            if (valueType != null && valueType.isEnum()) {
                this.source = Arrays.asList(valueType.getEnumConstants());
            }
        }

        if (getIterator().hasNext()) {
            for (int i = 0; i < this.offset && getIterator().hasNext(); i++) {
                getIterator().next();
            }
            return true;
        }
        this.cleanupBlock = this.empty;
        return false;
    }

    @BeginRender
    void bodyStart(MarkupWriter writer) {
        this.value = getIterator().next(); // get next value

        if (this.element != null) {
            writer.element(this.element);
            this.resources.renderInformalParameters(writer);
        }
    }

    @AfterRender
    boolean bodyEnd(MarkupWriter writer) {
        if (this.element != null) {
            writer.end();
        }
        this.index++;
        if (canAdvance()) {
            if (null != this.separator) {
                writer.writeRaw(this.separator);
            }
            return false;
        } else if (getIterator().hasNext()) {
            this.cleanupBlock = this.ellipsis;
        }
        return true;
    }

    @CleanupRender
    Block cleanupRender() {
        return this.cleanupBlock;
    }
}