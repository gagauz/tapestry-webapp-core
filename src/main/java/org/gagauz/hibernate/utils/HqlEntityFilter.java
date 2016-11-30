package org.gagauz.hibernate.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gagauz.utils.C;
import org.gagauz.utils.Function;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;

public class HqlEntityFilter {
    public static enum OrderMode {
        ASC,
        DESC
    }

    public static enum SqlOp {
        IN,
        EQ,
        LIKE,
        NE,
        GT,
        GE,
        LT,
        LE,
        BETWEEN,
        IS_NULL,
        IS_NOT_NULL,
        SQL
    }

    static class Triple {
        final SqlOp func;
        final String property;
        final Object[] value;

        public Triple(SqlOp func, String property, Object... value) {
            this.func = func;
            this.property = property;
            this.value = value;
        }
    }

    protected int indexFrom = -1;
    protected int indexTo = -1;
    private Map<String, OrderMode> orderBy = new LinkedHashMap<>();
    private List<Triple> criterias = C.arrayList();

    public HqlEntityFilter in(String name, Collection<?> value) {
        criterias.add(new Triple(SqlOp.IN, name, value));
        return this;
    }

    public HqlEntityFilter eq(String name, Object value) {
        criterias.add(new Triple(SqlOp.EQ, name, value));
        return this;
    }

    public HqlEntityFilter ne(String name, Object value) {
        criterias.add(new Triple(SqlOp.NE, name, value));
        return this;
    }

    public HqlEntityFilter like(String name, Object value) {
        criterias.add(new Triple(SqlOp.LIKE, name, value));
        return this;
    }

    public HqlEntityFilter ge(String name, Object value) {
        criterias.add(new Triple(SqlOp.GE, name, value));
        return this;
    }

    public HqlEntityFilter le(String name, Object value) {
        criterias.add(new Triple(SqlOp.LE, name, value));
        return this;
    }

    public HqlEntityFilter gt(String name, Object value) {
        criterias.add(new Triple(SqlOp.GT, name, value));
        return this;
    }

    public HqlEntityFilter lt(String name, Object value) {
        criterias.add(new Triple(SqlOp.LT, name, value));
        return this;
    }

    public HqlEntityFilter between(String name, Object value1, Object value2) {
        criterias.add(new Triple(SqlOp.BETWEEN, name, value1, value2));
        return this;
    }

    public HqlEntityFilter isNull(String name) {
        criterias.add(new Triple(SqlOp.IS_NULL, name));
        return this;
    }

    public HqlEntityFilter isNotNull(String name) {
        criterias.add(new Triple(SqlOp.IS_NOT_NULL, name));
        return this;
    }

    public HqlEntityFilter sql(String sql) {
        criterias.add(new Triple(SqlOp.SQL, sql));
        return this;
    }

    public HqlEntityFilter limit(int limit) {
        indexTo = limit;
        return this;
    }

    public HqlEntityFilter limit(int from, int limit) {
        indexFrom = from;
        indexTo = from + limit;
        return this;
    }

    public HqlEntityFilter orderAsc(String column) {
        orderBy.put(column, OrderMode.ASC);
        return this;
    }

    public HqlEntityFilter orderDecs(String column) {
        orderBy.put(column, OrderMode.DESC);
        return this;
    }

    public Query createQuery(Function<String, Query> queryFunc, String sql) {

        int n = 0;
        if (!criterias.isEmpty()) {
            sql += " where ";

            for (Triple c : criterias) {
                if (c.func == SqlOp.IN) {
                    sql += c.property + " in (:p" + n + ")";
                }
                if (c.func == SqlOp.EQ) {
                    sql += c.property + " = :p" + n;
                }
                if (c.func == SqlOp.NE) {
                    sql += c.property + " <> :p" + n;
                }
                if (c.func == SqlOp.GE) {
                    sql += c.property + " >= :p" + n;
                }
                if (c.func == SqlOp.LE) {
                    sql += c.property + " <= :p" + n;
                }
                if (c.func == SqlOp.GT) {
                    sql += c.property + " > :p" + n;
                }
                if (c.func == SqlOp.LT) {
                    sql += c.property + " < :p" + n;
                }
                if (c.func == SqlOp.LIKE) {
                    sql += c.property + " like :p" + n;
                }
                if (c.func == SqlOp.BETWEEN) {
                    sql += c.property + " between :p" + n + " and :p" + ++n;
                }
                if (c.func == SqlOp.IS_NULL) {
                    sql += c.property + " is null";
                }
                if (c.func == SqlOp.IS_NOT_NULL) {
                    sql += c.property + " is not null";
                }
                if (c.func == SqlOp.SQL) {
                    sql += c.property;
                }

                sql += " and ";

                n++;
            }

            sql = sql.substring(0, sql.length() - 5);
        }
        System.out.println(sql);

        if (!orderBy.isEmpty()) {

            Iterator<Entry<String, OrderMode>> it = orderBy.entrySet().iterator();
            Entry<String, OrderMode> e = it.next();
            sql += " order by " + e.getKey() + " " + e.getValue();
            while (it.hasNext()) {
                sql += ", " + e.getKey() + " " + e.getValue();
            }
        }
        System.out.println(sql);
        Query q = queryFunc.call(sql);
        n = 0;
        for (Triple c : criterias) {
            if (c.func == SqlOp.IN) {
                q.setParameterList("p" + n, (Collection) c.value[0]);
                System.out.println(":p" + n + " = " + c.value[0]);
            } else {
                for (int i = 0; i < c.value.length; i++) {

                    if (null == c.value[i]) {
                        q.setParameter("p" + n, null);
                    } else if (c.value[i] instanceof Integer) {
                        q.setParameter("p" + n, c.value[i], StandardBasicTypes.INTEGER);
                    } else if (c.value[i] instanceof Long) {
                        q.setParameter("p" + n, c.value[i], StandardBasicTypes.LONG);
                    } else if (c.value[i] instanceof BigDecimal) {
                        q.setParameter("p" + n, c.value[i], StandardBasicTypes.BIG_DECIMAL);
                    } else if (c.value[i] instanceof BigInteger) {
                        q.setParameter("p" + n, c.value[i], StandardBasicTypes.BIG_INTEGER);
                    } else if (c.value[i] instanceof Double) {
                        q.setParameter("p" + n, c.value[i], StandardBasicTypes.DOUBLE);
                    } else {
                        q.setParameter("p" + n, c.value[i]);
                    }
                    System.out.println(":p" + n + " = " + c.value[i]);
                    n += i;
                }
            }
            n++;

        }

        if (indexTo > 0 && indexFrom >= 0) {
            q.setFirstResult(indexFrom);
            q.setMaxResults(indexTo - indexFrom + 1);
        } else if (indexTo > 0) {
            q.setMaxResults(indexTo);
        }

        return q;
    }

    public void clearIndex() {
        indexFrom = -1;
        indexTo = -1;
    }

}
