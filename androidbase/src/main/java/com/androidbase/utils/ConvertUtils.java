package com.androidbase.utils;

/**
 * detail:转换工具类
 */
public class ConvertUtils {

    /**
     * Object 转 String
     *
     * @param object     Object
     * @param defaultStr 默认字符串
     * @return {@link String} 如果转换失败则返回 defaultStr
     */
    public static String toString(final Object object, final String defaultStr) {
        if (object != null) {
            try {
                if (object instanceof String) {
                    return (String) object;
                }
                if (object instanceof Integer) {
                    return Integer.toString((Integer) object);
                }
                if (object instanceof Boolean) {
                    return Boolean.toString((Boolean) object);
                }
                if (object instanceof Long) {
                    return Long.toString((Long) object);
                }
                if (object instanceof Double) {
                    return Double.toString((Double) object);
                }
                if (object instanceof Float) {
                    return Float.toString((Float) object);
                }
                if (object instanceof Byte) {
                    return Byte.toString((Byte) object);
                }
                if (object instanceof Character) {
                    return Character.toString((Character) object);
                }
                if (object instanceof Short) {
                    return Short.toString((Short) object);
                }
                return object.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return defaultStr;
    }

    /**
     * Object 转 Integer
     *
     * @param value Value
     * @return Integer
     */
    public static Integer toInt(final Object value) {
        return toInt(value, 0);
    }

    /**
     * Object 转 Integer
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Integer, 如果转换失败则返回 defaultValue
     */
    public static Integer toInt(final Object value, final Integer defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Integer) {
                return (Integer) value;
            }
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.indexOf(',') != 0) {
                    strVal = strVal.replaceAll(",", "");
                }
                return Integer.parseInt(strVal);
            }
            if (value instanceof Boolean) {
                return (Boolean) value ? 1 : 0;
            }
            throw new Exception("can not cast to int, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Object 转 Boolean
     *
     * @param value Value
     * @return Boolean
     */
    public static Boolean toBoolean(final Object value) {
        return toBoolean(value, false);
    }

    /**
     * Object 转 Boolean
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Boolean, 如果转换失败则返回 defaultValue
     */
    public static Boolean toBoolean(final Object value, final Boolean defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            if (value instanceof Number) {
                return ((Number) value).intValue() == 1;
            }
            if (value instanceof String) {
                String strVal = (String) value;
                if ("true".equalsIgnoreCase(strVal) || "1".equals(strVal)) {
                    return Boolean.TRUE;
                }
                if ("false".equalsIgnoreCase(strVal) || "0".equals(strVal)) {
                    return Boolean.FALSE;
                }
                // YES、TRUE
                if ("Y".equalsIgnoreCase(strVal) || "T".equalsIgnoreCase(strVal)) {
                    return Boolean.TRUE;
                }
                // NO、FALSE
                if ("N".equalsIgnoreCase(strVal) || "F".equalsIgnoreCase(strVal)) {
                    return Boolean.FALSE;
                }
            }
            throw new Exception("can not cast to boolean, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Object 转 Float
     *
     * @param value Value
     * @return Float
     */
    public static Float toFloat(final Object value) {
        return toFloat(value, 0f);
    }

    /**
     * Object 转 Float
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Float, 如果转换失败则返回 defaultValue
     */
    public static Float toFloat(final Object value, final Float defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Float) {
                return (Float) value;
            }
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.indexOf(',') != 0) {
                    strVal = strVal.replaceAll(",", "");
                }
                return Float.parseFloat(strVal);
            }
            throw new Exception("can not cast to float, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Object 转 Double
     *
     * @param value Value
     * @return Double
     */
    public static Double toDouble(final Object value) {
        return toDouble(value, 0d);
    }

    /**
     * Object 转 Double
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Double, 如果转换失败则返回 defaultValue
     */
    public static Double toDouble(final Object value, final Double defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Double) {
                return (Double) value;
            }
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.indexOf(',') != 0) {
                    strVal = strVal.replaceAll(",", "");
                }
                return Double.parseDouble(strVal);
            }
            throw new Exception("can not cast to double, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Object 转 Long
     *
     * @param value Value
     * @return Long
     */
    public static Long toLong(final Object value) {
        return toLong(value, 0L);
    }

    /**
     * Object 转 Long
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Long, 如果转换失败则返回 defaultValue
     */
    public static Long toLong(final Object value, final Long defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Long) {
                return (Long) value;
            }
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.indexOf(',') != 0) {
                    strVal = strVal.replaceAll(",", "");
                }
                return Long.parseLong(strVal);
            }
            throw new Exception("can not cast to long, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Object 转 Short
     *
     * @param value Value
     * @return Short
     */
    public static Short toShort(final Object value) {
        return toShort(value, (short) 0);
    }

    /**
     * Object 转 Short
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Short, 如果转换失败则返回 defaultValue
     */
    public static Short toShort(final Object value, final Short defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Short) {
                return (Short) value;
            }
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            }
            if (value instanceof String) {
                String strVal = (String) value;
                return Short.parseShort(strVal);
            }
            throw new Exception("can not cast to short, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Object 转 Character
     *
     * @param value Value
     * @return Character
     */
    public static Character toChar(final Object value) {
        return toChar(value, (char) 0);
    }

    /**
     * Object 转 Character
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Character, 如果转换失败则返回 defaultValue
     */
    public static Character toChar(final Object value, final Character defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Character) {
                return (Character) value;
            }
            if (value instanceof String) {
                String strVal = (String) value;
                if (strVal.length() == 1) {
                    return strVal.charAt(0);
                }
            }
            throw new Exception("can not cast to char, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    /**
     * Object 转 Byte
     *
     * @param value Value
     * @return Byte
     */
    public static byte toByte(final Object value) {
        return toByte(value, (byte) 0);
    }

    /**
     * Object 转 Byte
     *
     * @param value        Value
     * @param defaultValue 默认值
     * @return Byte, 如果转换失败则返回 defaultValue
     */
    public static byte toByte(final Object value, final Byte defaultValue) {
        if (value == null) {
            return defaultValue;
        }
        try {
            if (value instanceof Byte) {
                return (Byte) value;
            }
            if (value instanceof Number) {
                return ((Number) value).byteValue();
            }
            if (value instanceof String) {
                String strVal = (String) value;
                return Byte.parseByte(strVal);
            }
            throw new Exception("can not cast to byte, value : " + value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

}