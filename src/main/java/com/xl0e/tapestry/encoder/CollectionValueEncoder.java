package com.xl0e.tapestry.encoder;

import org.apache.tapestry5.ValueEncoder;

import java.util.Collection;

/**
 * CollectionValueEncoder implementation of ValueEncoder special for {@link MultiSelect}
 * to handle multiple values.
 * 
 * @author mg
 *
 * @param <T>
 */

public interface CollectionValueEncoder<T> extends ValueEncoder<T> {

    public Collection<T> toValues(String[] strings);
}
