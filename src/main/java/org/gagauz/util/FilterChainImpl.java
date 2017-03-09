package org.gagauz.util;

abstract public class FilterChainImpl<E> implements FilterChain<E> {
    @Override
    public final FilterChain<E> and(final Filter<E> and) {
        final FilterChain<E> $this = this;
        return new FilterChainImpl<E>() {
            @Override
            public boolean apply(E element) {
                return $this.apply(element) && and.apply(element);
            }
        };
    }

    @Override
    public final FilterChain<E> or(final Filter<E> or) {
        final FilterChain<E> $this = this;
        return new FilterChainImpl<E>() {
            @Override
            public boolean apply(E element) {
                return $this.apply(element) || or.apply(element);
            }
        };
    }

    @Override
    public final FilterChain<E> not() {
        final FilterChain<E> $this = this;
        return new FilterChainImpl<E>() {
            @Override
            public boolean apply(E element) {
                return !$this.apply(element);
            }
        };
    }
}
