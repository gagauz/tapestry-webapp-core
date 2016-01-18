package org.gagauz.tapestry.web.services.model;

import org.apache.tapestry5.internal.grid.CollectionGridDataSource;

import java.util.Collection;

/**
 * Workaround for {@link CollectionGridDataSource} limitation which
 * doesn't supports collections with objects of base and derived types.
 *
 * @param <T>
 * @author ewro
 * @see CollectionGridDataSource#getRowType()
 */
public final class CollectionGridDataSourceRowTypeFix<T> extends
        CollectionGridDataSource {

    private Class<?> rowType = null;

    public CollectionGridDataSourceRowTypeFix(Collection<T> source, Class<T> rowType) {
        super(source);
        this.rowType = rowType;
    }

    @Override
    public synchronized Class<?> getRowType() {
        return rowType;
    }
}
