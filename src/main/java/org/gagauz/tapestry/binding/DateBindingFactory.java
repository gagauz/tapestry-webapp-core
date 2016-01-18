package org.gagauz.tapestry.binding;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.tapestry5.Binding;
import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ioc.Location;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.services.BindingFactory;
import org.apache.tapestry5.services.BindingSource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateBindingFactory implements BindingFactory {

    private static final String DELIMITER = ",";
    private final BindingSource bindingSource;
    private final TypeCoercer resolver;
    private static FastDateFormat DEFAULT_FORMAT;
    private static final Map<String, FastDateFormat> FORMAT_MAP = new HashMap<String, FastDateFormat>();
    static {
        DEFAULT_FORMAT = getOrCreate("dd.MM.yyyy HH:mm");
    }

    private static FastDateFormat getOrCreate(String format) {
        FastDateFormat result = FORMAT_MAP.get(format);
        if (null == result) {
            synchronized (FORMAT_MAP) {
                result = FastDateFormat.getInstance(format);
                FORMAT_MAP.put(format, result);
            }
        }
        return result;
    }

    public DateBindingFactory(BindingSource bindingSource, TypeCoercer resolver) {
        this.bindingSource = bindingSource;
        this.resolver = resolver;
    }

    @Override
    public Binding newBinding(final String description, final ComponentResources container, ComponentResources component, String expression, Location location) {
        final String[] parts = expression.split(DELIMITER, 2);

        return new AbstractContextBinding(bindingSource, resolver, description, container) {
            @Override
            public Object get() {
                FastDateFormat format = parts.length == 1 ? DEFAULT_FORMAT : getOrCreate(parts[1]);
                Date conditionValue = getValue(parts[0], BindingConstants.PROP, Date.class);
                if (conditionValue != null) {
                    return format.format(conditionValue);
                }
                return "";
            }

        };
    }

}
