package io.github.cotrin8672.cem.util;

public final class AnvilCompatibilityContext {
    private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

    private AnvilCompatibilityContext() {
    }

    public static void push() {
        DEPTH.set(DEPTH.get() + 1);
    }

    public static void pop() {
        int depth = DEPTH.get();
        if (depth <= 1) {
            DEPTH.remove();
            return;
        }
        DEPTH.set(depth - 1);
    }

    public static boolean isActive() {
        return DEPTH.get() > 0;
    }
}
