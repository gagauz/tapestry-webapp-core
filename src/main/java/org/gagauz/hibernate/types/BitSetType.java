package org.gagauz.hibernate.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.BitSet;
import java.util.EnumSet;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public class BitSetType implements UserType {

    private static final int SQL_TYPE = Types.BIGINT;
    private static final int[] SQL_TYPES = new int[] {SQL_TYPE};

    private static final Class<?> RETURNED_CLASS = BitSet.class;

    @Override
    public int[] sqlTypes() {
        return SQL_TYPES;
    }

    @Override
    public Object nullSafeGet(ResultSet resultSet, String[] names, SharedSessionContractImplementor session, Object owner)
            throws HibernateException, SQLException {
        long value = resultSet.getLong(names[0]);
        if (resultSet.wasNull()) {
            return null;
        }
        final BitSet result = new BitSet(32);
        for (byte i = 0; value > 0; i++, value = (value >>> 1)) {
            result.set(i, (value & 1) > 0);
        }

        return result;
    }

    @Override
    public Class<?> returnedClass() {
        return RETURNED_CLASS;
    }

    @Override
    public boolean equals(Object x, Object y) throws HibernateException {
        return x != null ? x.equals(y) : y == null;
    }

    @Override
    public int hashCode(Object x) throws HibernateException {
        return x != null ? x.hashCode() : 0;
    }

    @Override
    public Object deepCopy(Object value) throws HibernateException {
        return value != null ? EnumSet.copyOf((EnumSet<?>) value) : null;
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

    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index, SharedSessionContractImplementor session)
            throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, sqlTypes()[0]);
        } else {
            final BitSet bitSet = (BitSet) value;

            long longValue = 0;

            for (byte b = 0; b < bitSet.length(); b++) {
                if (bitSet.get(b)) {
                    longValue = longValue | (1L << b);
                }
            }
            st.setLong(index, longValue);
        }
    }
}
