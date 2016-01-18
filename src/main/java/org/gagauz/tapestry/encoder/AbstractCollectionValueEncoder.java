package org.gagauz.tapestry.encoder;

import org.apache.tapestry5.OptionGroupModel;
import org.apache.tapestry5.OptionModel;
import org.apache.tapestry5.util.AbstractSelectModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CollectionValueEncoder implementation of ValueEncoder special for {@link MultiSelect}
 * to handle multiple values.
 * 
 * @author mg
 *
 * @param <T>
 */

public abstract class AbstractCollectionValueEncoder<T> extends AbstractSelectModel implements CollectionValueEncoder<T> {

    @SuppressWarnings("unchecked")
    @Override
    public Collection<T> toValues(String[] strings) {
        if (null == strings) {
            return createCollection(0);
        }
        Collection<T> result = createCollection(strings.length);
        for (String string : strings) {
            T value = toValue(string);
            if (value instanceof Collection) {
                result.addAll((Collection<? extends T>) value);
            } else {
                result.add(value);
            }
        }

        return result;
    }

    @Override
    public String toClient(T value) {
        return String.valueOf(value);
    }

    @Override
    public abstract T toValue(String string);

    protected Collection<T> createCollection(int size) {
        return new ArrayList<T>(size);
    }

    @Override
    public List<OptionGroupModel> getOptionGroups() {
        return null;
    }

    @Override
    public List<OptionModel> getOptions() {
        return null;
    }
}
