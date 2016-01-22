package org.gagauz.hibernate.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.gagauz.hibernate.model.base.Serializer;
import org.gagauz.utils.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

public abstract class CollectionType<T> implements UserType, ParameterizedType {

    public static final String CLASS = "class";
    public static final String SERIALIZER = "serializer";

    private static final int SQL_TYPE = Types.VARCHAR;
    private static final int[] SQL_TYPES = new int[] {SQL_TYPE};
    private static final String SEPARATOR_STRING = new Character((char) 0x1F).toString();

    // Default is string collection
    private Class<T> entityClass;
    private Serializer<T> serializer;

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public void setParameterValues(Properties parameters) {
        final String clazzName = parameters.getProperty(CLASS);
        if (null != clazzName) {
            try {
                entityClass = (Class<T>) Class.forName(clazzName);
            } catch (Exception e) {
                throw new IllegalArgumentException("Class " + clazzName + " not found", e);
            }
        }

        final String serializerName = parameters.getProperty(SERIALIZER);
        if (null != serializerName) {
            try {
                serializer = (Serializer<T>) Class.forName(serializerName).newInstance();
            } catch (Exception e) {
                throw new IllegalArgumentException("Serializer class " + serializerName + " not found", e);
            }
        } else {
            throw new IllegalStateException("No serializer is defined for class " + entityClass);
        }
    }

    @Override
    public Class<T> returnedClass() {
        return entityClass;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner) throws HibernateException,
            SQLException {
        final String string = resultSet.getString(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }

        if (StringUtils.isEmpty(string)) {
            return createCollection(entityClass, 0);
        }
        String[] strings = string.split(SEPARATOR_STRING);
        Collection<T> result = createCollection(entityClass, strings.length);
        for (String str : strings) {
            result.add(serializer.unserialize(str, entityClass));
        }

        return result;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if (null == x || null == y) {
            return false;
        }
        return x.equals(y);
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return (null == x || !(x instanceof Collection)) ? 0 : ((Collection) x).hashCode();
    }

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException,
            SQLException {

        if (null == value) {
            st.setNull(index, sqlTypes()[0]);
        } else if (!(value instanceof Collection)) {
            throw new HibernateException("Type of value [" + value.getClass() + "] is not Collection");
        } else {
            Iterator<T> i = ((Collection<T>) value).iterator();
            if (i.hasNext()) {
                StringBuilder sb = new StringBuilder();
                sb.append(serializer.serialize(i.next()));
                while (i.hasNext()) {
                    sb.append(SEPARATOR_STRING).append(serializer.serialize(i.next()));
                }
                st.setString(index, sb.toString());
            } else {
                st.setString(index, "");
            }
        }
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        if (null != value) {
            Collection<T> source = (Collection<T>) value;
            Collection<T> destination = createCollection(entityClass, source.size());
            destination.addAll(source);
            return destination;
        }

        return null;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public abstract Collection<T> createCollection(Class<T> class1, int size);
}
