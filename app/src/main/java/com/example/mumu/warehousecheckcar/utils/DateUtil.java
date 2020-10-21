package com.example.mumu.warehousecheckcar.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期工具類
 *
 * @author lidabo
 */
public class DateUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
        return sdf.format(date);
    }

    public static String formatDate(String timeStamp, String pattern) {
        if (timeStamp == null || timeStamp.length() == 0) {
            return "";
        }
        return formatDate(new Date(Long.parseLong(timeStamp)), pattern);
    }

    public static Date parseDate(String dateStr, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String currentTime() {
        return formatDate(new Date(System.currentTimeMillis()), YYYY_MM_DD_HH_MM_SS);
    }

    public static String currentDate() {
        return formatDate(new Date(System.currentTimeMillis()), YYYY_MM_DD);
    }

    /**
     * 指定日期改月数
     *
     * @param date        指定日期
     * @param offsetMonth 修改的月数偏移量
     * @return 2019-09-09 -> 2019-10-09
     */
    public static String changeMonth(Date date, int offsetMonth) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.CHINA);
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(date);
            rightNow.add(Calendar.MONTH, offsetMonth);
            Date dt1 = rightNow.getTime();
            return sdf.format(dt1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DateUtil.formatDate(new Date(), YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 根据Date获取Calendar
     *
     * @param date date
     * @return Calendar
     */
    public static Calendar createCalendarByDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        return calendar;
    }

    /**
     * 时间戳转成时间
     *
     * @param timeStamp 时间戳
     * @param pattern   格式
     * @return 2019-08-24 09:43
     */
    public static String timeStampToDate(long timeStamp, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
        Date date = new Date(timeStamp);
        return sdf.format(date);
    }

    /**
     * 获得今天日期
     *
     * @return 2019-08-29
     */
    public static String getNowDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.CHINA);
        return sdf.format(new Date());
    }

    /**
     * 两种格式的日期转换
     *
     * @param dates       时间
     * @param fromPattern 原格式
     * @param toPattern   目标格式
     * @return 2019-09-09 12:11:00 -> 2019-09-09
     */
    public static String translateDate(String dates, String fromPattern, String toPattern) {
        if (TextUtils.isEmpty(dates)) {
            return "";
        }
        SimpleDateFormat from = new SimpleDateFormat(fromPattern, Locale.CHINA);
        SimpleDateFormat to = new SimpleDateFormat(toPattern, Locale.CHINA);
        try {
            Date date = from.parse(dates);
            return to.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 格式化日期格式(默认为 yyyy-MM-dd HH:mm:ss)
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param minute 分
     * @return 2019-08-23 16:10:00
     */
    public static String date2Time(int year, int month, int day, int hour, int minute) {
        return date2Time(year, month, day, hour, minute, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 格式化日期格式
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param minute 分
     * @return 2019-08-23 16:10:00
     */
    public static String date2Time(int year, int month, int day, int hour, int minute, String pattern) {
        Date date = getDateByTime(year, month, day, hour, minute);
        return formatDate(date, pattern);
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param time    日期
     * @param pattern 格式
     * @return 时间戳
     */
    public static long date2TimeStamp(String time, String pattern) {
        Date date = parseDate(time, pattern);
        return date.getTime();
    }

    /**
     * 通过年月日获取Date
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return Date
     */
    private static Date getDateByTime(int year, int month, int day) {
        return getDateByTime(year, month, day, 0, 0);
    }

    /**
     * 通过年月日时分获取Date
     *
     * @param year   年
     * @param month  月
     * @param day    日
     * @param hour   时
     * @param minute 分
     * @return Date
     */
    private static Date getDateByTime(int year, int month, int day, int hour, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        return c.getTime();
    }
}