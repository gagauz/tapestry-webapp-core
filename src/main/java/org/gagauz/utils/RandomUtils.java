package org.gagauz.utils;


import java.util.*;

public class RandomUtils {
    private static Random random = new Random(System.currentTimeMillis());

    private RandomUtils() {
    }

    public static <T> List<T> getRandomSubList(Collection<T> input, int subsetSize) {
        Random r = new Random();
        int inputSize = input.size();
        subsetSize = Math.min(inputSize, subsetSize);
        List<T> source = C.arrayList(input);
        for (int i = 0; i < subsetSize; i++) {
            int indexToSwap = i + r.nextInt(inputSize - i);
            T temp = source.get(i);
            source.set(i, source.get(indexToSwap));
            source.set(indexToSwap, temp);
        }
        return inputSize > subsetSize ? source.subList(0, subsetSize) : source;
    }

    public static <T> List<T> getRandomSubList(T[] values, int subsetSize) {
        return getRandomSubList(Arrays.asList(values), subsetSize);
    }

    public static int nextInt(int n) {
        return random.nextInt(n);
    }

    public static boolean nextBool() {
        return random.nextBoolean();
    }

    public static <T> T getRandomObject(T... objects) {
        int i = random.nextInt(objects.length);
        return objects[i];
    }

    public static <T> T getRandomObject(Collection<T> objects) {
        if (null != objects && !objects.isEmpty()) {
            int i = random.nextInt(objects.size());
            if (objects instanceof List) {
                return ((List<T>) objects).get(i);
            }
            Iterator<T> it = objects.iterator();
            for (int c = 0; c <= i; c++) {
                if (c == i) {
                    return it.next();
                } else {
                    it.next();
                }
            }
        }
        return null;
    }

}
