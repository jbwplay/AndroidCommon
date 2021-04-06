package com.androidbase.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 */
/*                                            HH:mm    15:44
 *                                            h:mm a    3:44 下午
 *                                           HH:mm z    15:44 CST
 *                                           HH:mm Z    15:44 +0800
 *                                        HH:mm zzzz    15:44 中国标准时间
 *                                          HH:mm:ss    15:44:40
 *                                        yyyy-MM-dd    2016-08-12
 *                                  yyyy-MM-dd HH:mm    2016-08-12 15:44
 *                               yyyy-MM-dd HH:mm:ss    2016-08-12 15:44:40
 *                          yyyy-MM-dd HH:mm:ss zzzz    2016-08-12 15:44:40 中国标准时间
 *                     EEEE yyyy-MM-dd HH:mm:ss zzzz    星期五 2016-08-12 15:44:40 中国标准时间
 *                          yyyy-MM-dd HH:mm:ss.SSSZ    2016-08-12 15:44:40.461+0800
 *                        yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
 *                      yyyy.MM.dd G 'at' HH:mm:ss z    2016.08.12 公元 at 15:44:40 CST
 *                                            K:mm a    3:44 下午
 *                                  EEE, MMM d, ''yy    星期五, 八月 12, '16
 *                             hh 'o''clock' a, zzzz    03 o'clock 下午, 中国标准时间
 *                      yyyyy.MMMMM.dd GGG hh:mm aaa    02016.八月.12 公元 03:44 下午
 *                        EEE, d MMM yyyy HH:mm:ss Z    星期五, 12 八月 2016 15:44:40 +0800
 *                                     yyMMddHHmmssZ    160812154440+0800
 *                        yyyy-MM-dd'T'HH:mm:ss.SSSZ    2016-08-12T15:44:40.461+0800
 * EEEE 'DATE('yyyy-MM-dd')' 'TIME('HH:mm:ss')' zzzz    星期五 DATE(2016-08-12) TIME(15:44:40) 中国标准时间
 * 注意：SimpleDateFormat不是线程安全的，线程安全需用{@code ThreadLocal<SimpleDateFormat>}
 */
public class DateUtils {

    public static final int MSEC = 1;

    public static final int SEC = 1000;

    public static final int MIN = 60000;

    public static final int HOUR = 3600000;

    public static final int DAY = 86400000;

    public static final String dateFormatYMDHMS = "yyyy-MM-dd HH:mm:ss";

    public static final String dateFormatYMD = "yyyy-MM-dd";

    public static final String dateFormatYM = "yyyy-MM";

    public static final String dateFormatYMDHM = "yyyy-MM-dd HH:mm";

    public static final String dateFormatMD = "MM/dd";

    public static final String dateFormatHMS = "HH:mm:ss";

    public static final String dateFormatHM = "HH:mm";

    public static final String dateFormatMS = "mm:ss";

    public static String getCurrentDate(String format) {
        String curDateTime = null;
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            Calendar c = new GregorianCalendar();
            curDateTime = mSimpleDateFormat.format(c.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return curDateTime;
    }

    public static String getStringByFormat(long milliseconds, String format) {
        String thisDateTime = null;
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(format);
            thisDateTime = mSimpleDateFormat.format(new Date(milliseconds));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thisDateTime;
    }

    /**
     * 获取GMT服务端时间为本地时间
     *
     * @param dateAndTime
     * @param format
     * @return
     */
    public static String getServiceGmtDateString(String dateAndTime, String format) {
        Date date = parseGMTDate(dateAndTime);
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(date);
    }

    public static Date parseGMTDate(String date) {
        if (date == null) {
            return null;
        }

        StringBuilder sbDate = new StringBuilder();
        sbDate.append(date);
        String newDate = null;
        Date dateDT = null;
        try {
            newDate = sbDate.substring(0, "yyyy/MM/dd HH:mm:ss".length()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*yyyy-MM-dd'T'HH:mm:ss*/
        String rDate = newDate.replace("T", " ");
        String nDate = rDate.replaceAll("-", "/");

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            dateDT = sdf.parse(nDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateDT;
    }

    /*
     * Converts UTC time formatted as ISO to device local time.
     * T代表后面跟着是时间，Z代表0时区
     * @param utcDate
     * @return Date
     * @throws Exception
     */
    public static Date toLocalTime(String utcDate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale
                .getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date localDate = sdf.parse(utcDate);
        // Convert UTC Time to Local
        sdf.setTimeZone(TimeZone.getDefault());
        String dateFormateInUTC = sdf.format(localDate);
        return sdf.parse(dateFormateInUTC);
    }

    /**
     * 毫秒时间戳转合适时间长度
     *
     * @param millis    毫秒时间戳
     * @param precision
     * @return 合适时间长度
     * @精度precision = 0返回null
     * @precision = 1返回天
     * @precision = 2返回天和小时
     * @precision = 3返回天、小时和分钟
     * @precision = 4返回天、小时、分钟和秒
     * @precision = 5返回天、小时、分钟、秒和毫秒
     */
    public static String millis2FitTimeSpan(long millis, int precision) {
        if (millis <= 0 || precision <= 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        int[] unitLen = {86400000, 3600000, 60000, 1000, 1};
        precision = Math.min(precision, 5);
        for (int i = 0; i < precision; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                millis -= mode * unitLen[i];
                sb.append(mode).append(units[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 获取友好型与当前时间的差
     *
     * @param millis 毫秒时间戳
     * @return 友好型与当前时间的差
     * @如果小于1秒钟内，显示刚刚</li>
     * @如果在1分钟内，显示XXX秒前</li>
     * @如果在1小时内，显示XXX分钟前</li>
     * @如果在1小时外的今天内，显示今天15:32</li>
     * @如果是昨天的，显示昨天15:32</li>
     * @其余显示，2016-10-15</li>
     * @时间不合法的情况全部日期和时间信息，如星期六 十月 27 14:21:20 CST 2007
     */
    public static String getFriendlyTimeSpanByNow(final long millis) {
        long now = System.currentTimeMillis();
        long span = now - millis;
        if (span < 0) {
            return String.format("%tc", millis);// U can read http://www.apihome.cn/api/java/Formatter.html to understand it.
        }
        if (span < 1000) {
            return "刚刚";
        } else if (span < MIN) {
            return String.format(Locale.getDefault(), "%d秒前", span / SEC);
        } else if (span < HOUR) {
            return String.format(Locale.getDefault(), "%d分钟前", span / MIN);
        }
        // 获取当天00:00
        long wee = getWeeOfToday();
        if (millis >= wee) {
            return String.format("今天%tR", millis);
        } else if (millis >= wee - DAY) {
            return String.format("昨天%tR", millis);
        } else {
            return String.format("%tF", millis);
        }
    }

    private static long getWeeOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * @param millis
     * @return
     */
    public static long getNextDayMs(long millis) {
        if (millis > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(millis);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            return calendar.getTimeInMillis();
        }
        return 0;
    }

}
