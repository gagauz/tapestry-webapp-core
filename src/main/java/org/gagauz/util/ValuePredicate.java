package org.gagauz.util;

public abstract class ValuePredicate<V, E> {

    abstract public boolean apply(V value, E element);

    public final <X> ValuePredicate<V, E> and(final ValuePredicate<X, E> andPredicate, final X andValue) {
        final ValuePredicate<V, E> $this = this;
        return new ValuePredicate<V, E>() {
            @Override
            public boolean apply(V value, E element) {
                return $this.apply(value, element) && andPredicate.apply(andValue, element);
            }
        };
    }

    public final <X> ValuePredicate<V, E> or(final ValuePredicate<X, E> orPredicate, final X orValue) {
        final ValuePredicate<V, E> $this = this;
        return new ValuePredicate<V, E>() {
            @Override
            public boolean apply(V value, E element) {
                return $this.apply(value, element) || orPredicate.apply(orValue, element);
            }

        };
    }
}
