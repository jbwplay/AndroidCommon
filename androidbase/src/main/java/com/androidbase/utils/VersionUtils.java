package com.androidbase.utils;

public class VersionUtils {

    /**
     * 当前版本号<=force_version<=latest_version------------强制更新 force_version<当前版本号<latest_version--------------可选更新
     * 当前版本号=latest_version 不需要更新
     * @param curVersion 服务器版本号
     * @param serVersion app版本号
     * @param needContains 当服务器版本号为forceVersion时，这个值传true。
     * @return true 满足更新条件；false 不满足
     */
    public static boolean needUpdate(String curVersion, String serVersion, boolean needContains) {
        int version = VersionComparison(serVersion, curVersion);
        if (needContains) {
            if (version == 1 || version == 0) {
                return true;
            }
        } else {
            if (version == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return if version1 > version2, return 1, if equal, return 0, else return -1
     */
    public static int VersionComparison(String versionServer, String versionLocal) {
        String version1 = versionServer.replace("v", "").replace("V", "");
        String version2 = versionLocal.replace("v", "").replace("V", "");
        if (version1 == null || version1.length() == 0 || version2 == null || version2.length() == 0) {
            throw new IllegalArgumentException("Invalid parameter!");
        }

        int index1 = 0;
        int index2 = 0;
        while (index1 < version1.length() && index2 < version2.length()) {
            int[] number1 = getValue(version1, index1);
            int[] number2 = getValue(version2, index2);

            if (number1[0] < number2[0]) {
                return -1;
            } else if (number1[0] > number2[0]) {
                return 1;
            } else {
                index1 = number1[1] + 1;
                index2 = number2[1] + 1;
            }
        }
        if (index1 == version1.length() && index2 == version2.length()) {
            return 0;
        }
        if (index1 < version1.length()) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * @param version
     * @param index the starting point
     * @return the number between two dots, and the index of the dot
     */
    public static int[] getValue(String version, int index) {
        int[] value_index = new int[2];
        StringBuilder sb = new StringBuilder();
        while (index < version.length() && version.charAt(index) != '.') {
            sb.append(version.charAt(index));
            index++;
        }
        value_index[0] = Integer.parseInt(sb.toString());
        value_index[1] = index;

        return value_index;
    }

}
