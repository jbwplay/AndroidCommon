package com.androidbase.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class MiscUtils {

    /**
     * Assert a boolean expression, throwing <code>IllegalArgumentException</code> if the
     * test result is <code>false</code>.
     *
     * @param expression a boolean expression
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert a boolean expression, throwing <code>IllegalArgumentException</code> if the
     * test result is <code>false</code>.
     *
     * @param expression a boolean expression
     * @throws IllegalArgumentException if expression is <code>false</code>
     */
    public static void isTrue(boolean expression) {
        isTrue(expression, "[Assertion failed] - this expression must be true");
    }

    /**
     * Assert that an object is <code>null</code> .
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is not <code>null</code>
     */
    public static void isNull(Object object, String message) {
        if (object != null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is <code>null</code> .
     *
     * @param object the object to check
     * @throws IllegalArgumentException if the object is not <code>null</code>
     */
    public static void isNull(Object object) {
        isNull(object, "[Assertion failed] - the object argument must be null");
    }

    /**
     * Assert that an object is not <code>null</code> .
     *
     * @param object  the object to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an object is not <code>null</code> .
     *
     * @param object the object to check
     * @throws IllegalArgumentException if the object is <code>null</code>
     */
    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not " + "be" + " null");
    }

    public static void notEmpty(CharSequence text) {
        notEmpty(text, "[Assertion failed] - this string must not be null or empty.");
    }

    public static void notEmpty(CharSequence text, String message) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T extends CharSequence> T notBlank(final T argument, final String name) {
        if (argument == null) {
            throw new IllegalArgumentException(name + " may not be null");
        }
        if (StringUtils.isBlank(argument)) {
            throw new IllegalArgumentException(name + " may not be blank");
        }
        return argument;
    }

    /**
     * Assert that an array has elements; that is, it must not be <code>null</code> and
     * must have at least one element.
     *
     * @param array   the array to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the object array is <code>null</code> or has no
     *                                  elements
     */
    public static void notEmpty(Object[] array, String message) {
        if ((array == null || array.length == 0)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that an array has elements; that is, it must not be <code>null</code> and
     * must have at least one element.
     *
     * @param array the array to check
     * @throws IllegalArgumentException if the object array is <code>null</code> or has no
     *                                  elements
     */
    public static void notEmpty(Object[] array) {
        notEmpty(array, "[Assertion failed] - this array must not be empty: it must " + "contain at least 1 element");
    }

    /**
     * Assert that a collection has elements; that is, it must not be <code>null</code>
     * and must have at least one element.
     *
     * @param collection the collection to check
     * @param message    the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the collection is <code>null</code> or has no
     *                                  elements
     */
    public static void notEmpty(Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that a collection has elements; that is, it must not be <code>null</code>
     * and must have at least one element.
     *
     * @param collection the collection to check
     * @throws IllegalArgumentException if the collection is <code>null</code> or has no
     *                                  elements
     */
    public static void notEmpty(Collection<?> collection) {
        notEmpty(collection, "[Assertion failed] - this collection must not be empty: " + "it must contain at least 1 element");
    }

    /**
     * Assert that a Map has entries; that is, it must not be <code>null</code> and must
     * have at least one entry.
     *
     * @param map     the map to check
     * @param message the exception message to use if the assertion fails
     * @throws IllegalArgumentException if the map is <code>null</code> or has no entries
     */
    public static void notEmpty(Map<?, ?> map, String message) {
        if (map == null || map.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Assert that a Map has entries; that is, it must not be <code>null</code> and must
     * have at least one entry.
     *
     * @param map the map to check
     * @throws IllegalArgumentException if the map is <code>null</code> or has no entries
     */
    public static void notEmpty(Map<?, ?> map) {
        notEmpty(map, "[Assertion failed] - this map must not be empty; it must " + "contain" + " at least one entry");
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     *
     * @param clazz the required class
     * @param obj   the object to check
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance
     */
    public static void isInstanceOf(Class<?> clazz, Object obj) {
        isInstanceOf(clazz, obj, "");
    }

    /**
     * Assert that the provided object is an instance of the provided class.
     *
     * @param type    the type to check against
     * @param obj     the object to check
     * @param message a message which will be prepended to the message produced by the
     *                function itself, and which may be used to provide context. It should
     *                normally end in a ": " or ". " so that the function generate message
     *                looks ok when prepended to it.
     * @throws IllegalArgumentException if the object is not an instance of clazz
     * @see Class#isInstance
     */
    public static void isInstanceOf(Class<?> type, Object obj, String message) {
        notNull(type, "Type to check against must not be null");
        if (!type.isInstance(obj)) {
            throw new IllegalArgumentException(message + "Object of class [" + (obj != null ? obj
                    .getClass()
                    .getName() : "null") + "] must be an instance " + "of " + type);
        }
    }

    public static byte[] intToBytes(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }
        return b;
    }

    public static int byteArray2int(byte[] b) {
        return (((int) b[0]) << 24) + (((int) b[1]) << 16) + (((int) b[2]) << 8) + b[3];
    }

    public static int totalPage(int totalCount, int numPerPage) {
        if (numPerPage == 0) {
            return 0;
        }
        return totalCount % numPerPage == 0 ? (totalCount / numPerPage) : (totalCount / numPerPage + 1);
    }

    /**
     * 获取随机规则生成 UUID
     *
     * @return 随机规则生成 UUID
     */
    private static UUID getRandomUUID() {
        // 获取随机数
        String random1 = String.valueOf(900000 + new Random().nextInt(10000));
        // 获取随机数
        String random2 = String.valueOf(900000 + new Random().nextInt(10000));
        // 获取当前时间
        String time = System.currentTimeMillis() + random1 + random2;
        // 生成唯一随机 UUID
        return new UUID(time.hashCode(), ((long) random1.hashCode() << 32) | random2.hashCode());
    }

    /**
     * 获取随机规则生成 UUID 字符串
     *
     * @return 随机规则生成 UUID 字符串
     */
    public static String getRandomUUIDToString() {
        return getRandomUUID().toString().toLowerCase();
    }


}

