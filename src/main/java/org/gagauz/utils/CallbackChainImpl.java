package org.gagauz.utils;

public abstract class CallbackChainImpl<E> implements CallbackChain<E> {

    @Override
    public CallbackChain<E> append(final Callback<E> append) {
        final CallbackChain<E> $this = this;
        return new CallbackChainImpl<E>() {
            @Override
            public void call(E object) {
                $this.call(object);
                append.call(object);
            }
        };
    }

    @Override
    public CallbackChain<E> prepend(final Callback<E> prepend) {
        final CallbackChain<E> $this = this;
        return new CallbackChainImpl<E>() {
            @Override
            public void call(E object) {
                prepend.call(object);
                $this.call(object);
            }
        };
    }
}
