package com.danielworld.graph.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-09.
 */

public class ChartDateUtil {

	/**
	 * Get current day of week <br>
	 *     @see Calendar#SUNDAY
	 * @return
	 */
	public static ChartDays getDayOfWeek() {
		return getDayOfWeek(System.currentTimeMillis());
	}

	public static ChartDays getDayOfWeek(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return ChartDays.getDate(calendar.get(Calendar.DAY_OF_WEEK));
	}

	/**
	 * Get current day of month
	 * @return
	 */
	public static int getDayOfMonth() {
		return getDayOfMonth(System.currentTimeMillis());
	}

	public static int getDayOfMonth(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * Get current day of year
	 * @return
	 */
	public static int getDayOfYear() {
		return getDayOfYear(System.currentTimeMillis());
	}

	public static int getDayOfYear(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	/**
	 * Get week of month <br>
	 *     first week returns 1
	 * @return
	 */
	public static int getWeekOfMonth() {
		return getWeekOfMonth(System.currentTimeMillis());
	}

	public static int getWeekOfMonth(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.get(Calendar.WEEK_OF_MONTH);
	}


	/**
	 * Get current month of year
	 * @return
	 */
	public static int getMonthOfYear() {
		return getMonthOfYear(System.currentTimeMillis());
	}

	public static int getMonthOfYear(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		// Daniel (2017-02-09 15:46:42): Month starts with 0. so add 1
		return calendar.get(Calendar.MONTH) + 1;
	}

	/**
	 * Get current year
	 * @return
	 */
	public static int getYear() {
		return getYear(System.currentTimeMillis());
	}

	public static int getYear(long milliseconds){
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.get(Calendar.YEAR);
	}

	public static int getDaysOfMonthMaximum() {
		return getDaysOfMonthMaximum(System.currentTimeMillis());
	}

	public static int getDaysOfMonthMaximum(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	}

	public static int getDaysOfYearMaximum() {
		return getDaysOfYearMaximum(System.currentTimeMillis());
	}

	public static int getDaysOfYearMaximum(long milliseconds) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		return calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
	}

	/**
	 * add months from {@code milliseconds} and return new milliseconds
	 * @param milliseconds
	 * @param months if minus value then remove from {@code milliseconds}
	 * @return
	 */
	public static long addMonths(long milliseconds, int months) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		calendar.add(Calendar.MONTH, months);
		return calendar.getTimeInMillis();
	}

	/**
	 * add days from {@code milliseconds} and return new milliseconds
	 * @param milliseconds
	 * @param days	if minus value then remove from {@code milliseconds}
	 * @return
	 */
	public static long addDays(long milliseconds, int days) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.setTimeInMillis(milliseconds);
		calendar.add(Calendar.DAY_OF_MONTH, days);
		return calendar.getTimeInMillis();
	}

	/**
	 * Get day count between {@code startTime} and {@code endTime}
	 * @param startTime
	 * @param endTime
	 * @param includeTarget <b>true</b> : include day of {@code startTime} and {@code endTime}
	 * @return
	 */
	public static int getDayCount(long startTime, long endTime, boolean includeTarget) {
		// TODO: this is not perfect.. sorry.
		// TODO: Daniel (2017-02-20 18:50:04): This only works when the difference days only in 365 days..
		return Math.abs(getDayOfYear(startTime) - getDayOfYear(endTime)) + (includeTarget ? 1 : 0);
	}

	/**
	 * Get month count between {@code startTime} and {@code endTime}
	 * @param startTime
	 * @param endTime
	 * @param includeTarget <b>true</b> : include month of {@code startTime} and {@code endTime}
	 * @return
	 */
	public static int getMonthCount(long startTime, long endTime, boolean includeTarget) {
		int startYear = getYear(startTime);
		int startMonth = getMonthOfYear(startTime);

		int endYear = getYear(endTime);
		int endMonth = getMonthOfYear(endTime);

		if (startMonth <= endMonth) {
			return (endYear - startYear) * 12 + endMonth - startMonth + (includeTarget ? 1 : 0);
		}
		else {
			// startMonth 가 endMonth 보다 클 경우
			// 1. endTime 의 Year 에서 startTime 의 Year 를 뺀 뒤, 그 값 * 12 해서 endMonth 에 붙인다.
			// 2. 그 변형된 endMonth - startMonth 하면 됨
			endMonth += (endYear - startYear) * 12;
			return endMonth - startMonth + (includeTarget ? 1 : 0);
		}
	}

	/**
	 * Turn date(yyyy-MM-dd) to milliseconds
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public static long getMillisFromDate(int year, int month, int day) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		// Daniel (2017-02-09 15:46:42): Month starts with 0. so add -1
		calendar.set(year, month - 1, day);
		return calendar.getTimeInMillis();
	}

	/**
	 * Turn date(yyyy-MM-dd hh:mm:ss) to milliseconds
	 * @param year
	 * @param month
	 * @param day
	 * @param hour
	 * @param minute
	 * @param second
	 * @return
	 */
	public static long getMillisFromDate(int year, int month, int day, int hour, int minute, int second) {
		Calendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.set(year, month - 1, day, hour, minute, second);
		return calendar.getTimeInMillis();
	}

	/**
	 * Get Last week's time between Sunday to Saturday from now
	 * @return
	 */
	public static long[] getLastWeekTime() {
		return getLastWeekTime(System.currentTimeMillis());
	}

	/**
	 * Get Last week's time between Sunday to Saturday from {@code milliseconds}
	 * @param milliseconds
	 * @return long[0] : Sunday time, long[1] : Saturday time
	 */

	public static long[] getLastWeekTime(long milliseconds) {
		// Daniel (2017-02-09 16:31:38): Week starts from Sunday

		long lastDay = addDays(milliseconds, -7);
		int lastDate = getDayOfWeek(lastDay).value;

		// lastDate + x = 1 (sunday) // x = 1 - lastDate
		int addDays = 1 - lastDate;

		long lastSunday = addDays(lastDay, addDays);

		int targetYear = getYear(lastSunday);
		int targetMonth = getMonthOfYear(lastSunday);
		int targetDay = getDayOfMonth(lastSunday);

		long targetResult = getMillisFromDate(targetYear, targetMonth, targetDay, 0, 0, 0);

		return new long[]{targetResult, addDays(targetResult, 6)};
	}

	public static int getDanielMonthIndex(long milliseconds) {
		short standardYear = 1700;
		int targetYear = getYear(milliseconds);
		int targetMonth = getMonthOfYear(milliseconds);
		return (targetYear - standardYear) * 12 + targetMonth - 1;
	}

	public static long getTimeFromDanielMonthIndex(int monthIndex) {
		short standardYear = 1700;
		int getYear = monthIndex / 12;
		int getMonth = monthIndex % 12;
		return getMillisFromDate(standardYear + getYear, getMonth + 1);
	}

	/** @deprecated */
	@Deprecated
	public static long getMillisFromDate(int year, int month) {
		GregorianCalendar calendar = new GregorianCalendar(TimeZone.getDefault());
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		return calendar.getTimeInMillis();
	}

	public static int getDanielDayIndex(long milliseconds) {
		int year = getYear(milliseconds);
		int month = getMonthOfYear(milliseconds);
		int day = getDayOfMonth(milliseconds);
		return (int)(getMillisFromDate(year, month, day, 23, 59, 59) / 86400000L);
	}

	public static long getTimeFromDanielDayIndex(int dayIndex) {
		return (long)dayIndex * 86400000L;
	}

//	/**
//	 * Return an ISO 8601 combined date and time string for current date/time
//	 * @param time long time (milliseconds)
//	 * @return String with format "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
//	 */
//	public static String getDate(long time){
//		Date date = new Date(time);
//		return getDate(date, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
//	}

	/**
	 * Return an {@code format} combined date and time string for current date/time <br>
	 *     <p>default format is ISO 8601</p>
	 * @param time long time (milliseconds)
	 * @param format String with date format
	 * @return
	 */
	public static String getDate(long time, String format) {
		Date date = new Date(time);
		return getDate(date, format);
	}

	/**
	 * Return an ISO 8601 combined date and time string for specified date/time
	 *
	 * @param date {@link Date}
	 * @param format String with date format
	 * @return
	 */
	public static String getDate(Date date, String format) {
		String dateStringFormat = format;
		if (StringUtil.isNullorEmpty(dateStringFormat))
			dateStringFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

		DateFormat dateFormat = new SimpleDateFormat(dateStringFormat, Locale.US);
		dateFormat.setTimeZone(TimeZone.getDefault());
		return dateFormat.format(date);
	}
}
