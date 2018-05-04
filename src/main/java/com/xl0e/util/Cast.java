package com.xl0e.util;

import java.util.function.Consumer;
import java.util.function.Function;

public class Cast {

    private static Cast EMPTY = new EmptyCast(null);
    protected final Object obj;

    Cast() {
        this.obj = null;
    }

    Cast(Object obj) {
        this.obj = obj;
    }

    public static Cast of(Object obj) {
        if (null == obj) {
            return EMPTY;
        }
        return new Cast(obj);
    }

    public <Z> Cast cast(Class<Z> cls, Consumer<Z> consumer) {
        if (cls.isInstance(obj)) {
            consumer.accept((Z) obj);
            return EMPTY;
        }
        return this;
    }

    public <Z> Cast castF(Class<Z> cls, Function<Z, ?> callable) {
        if (cls.isInstance(obj)) {
            return new EmptyCast(callable.apply((Z) obj));
        }
        return this;
    }

    public void orElse(Consumer consumer) {
        consumer.accept(obj);
    }

    public <Z> Z orElseGet(Function<Object, Z> callable) {
        return callable.apply(obj);
    }

    public <Z> Z orElseGet(Z alt) {
        return alt;
    }

    public <Z> Z get() {
        throw new IllegalStateException("");
    }

    static class EmptyCast extends Cast {
        private Object capture;

        EmptyCast(Object capture) {
            this.capture = capture;
        }

        @Override
        public <Z> Cast cast(Class<Z> cls, Consumer<Z> consumer) {
            return this;
        }

        @Override
        public <Z> Cast castF(Class<Z> cls, Function<Z, ?> callable) {
            return this;
        }

        @Override
        public <Z> Z get() {
            return (Z) capture;
        }

        @Override
        public void orElse(Consumer consumer) {
        }

        @Override
        public <Z> Z orElseGet(Function<Object, Z> callable) {
            return (Z) capture;
        }

        @Override
        public <Z> Z orElseGet(Z alt) {
            return (Z) capture;
        }
    }

    public static void main(String[] args) {
        String obj = "";
        Cast.of(obj)
                .cast(Integer.class, x -> System.out.println("integer"))
                .cast(Byte.class, x -> System.out.println("byte"))
                .cast(String.class, x -> System.out.println("string"))
                .cast(Double.class, x -> System.out.println("double"))
                .orElse(x -> System.out.println("???"));

        Object obj1 = new Object();
        Cast.of(obj1)
                .cast(Integer.class, x -> System.out.println("integer"))
                .cast(Byte.class, x -> System.out.println("byte"))
                .cast(String.class, x -> System.out.println("string"))
                .cast(Double.class, x -> System.out.println("double"))
                .orElse(x -> System.out.println("???"));

        Object r = Cast.of("")
                .castF(Integer.class, x -> x + 1)
                .castF(Byte.class, x -> x + 2)
                .castF(String.class, x -> x + "1")
                .castF(Double.class, x -> x + 3)
                .orElseGet(x -> x);

        System.out.println(r);

    }
}
