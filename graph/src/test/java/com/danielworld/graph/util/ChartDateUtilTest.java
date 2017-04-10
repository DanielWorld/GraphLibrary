package com.danielworld.graph.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-09.
 */

public class ChartDateUtilTest {

    @Test
    public void testDayOfWeek() {

//		assertEquals(ChartDays.Saturday, ChartDateUtil.getDayOfWeek());

		long date = ChartDateUtil.getMillisFromDate(2017, 2, 1);
		assertEquals(ChartDays.Wednesday, ChartDateUtil.getDayOfWeek(date));

		long date2 = ChartDateUtil.getMillisFromDate(2017, 1, 31);
		assertEquals(ChartDays.Tuesday, ChartDateUtil.getDayOfWeek(date2));

		long date3 = ChartDateUtil.getMillisFromDate(2017, 2, 28);
		assertEquals(ChartDays.Tuesday, ChartDateUtil.getDayOfWeek(date3));

    }

	@Test
	public void testWeekOfMonth() {

//		assertEquals(2, ChartDateUtil.getWeekOfMonth(System.currentTimeMillis()));

		long date = ChartDateUtil.getMillisFromDate(2017, 1, 31);
		assertEquals(5, ChartDateUtil.getWeekOfMonth(date));
	}

	@Test
	public void testLastWeekTime() {

		long[] time = ChartDateUtil.getLastWeekTime(ChartDateUtil.getMillisFromDate(2017, 2, 9, 0, 0, 0));

		// Check month of year
		assertNotEquals(
				ChartDateUtil.getMonthOfYear(time[0]),
				ChartDateUtil.getMonthOfYear(time[1]));

		// Check day of month
		assertEquals(29, ChartDateUtil.getDayOfMonth(time[0]));
		assertEquals(4, ChartDateUtil.getDayOfMonth(time[1]));

		// Check week of month
		assertEquals(5, ChartDateUtil.getWeekOfMonth(time[0]));
		assertEquals(1, ChartDateUtil.getWeekOfMonth(time[1]));

	}

	@Test
	public void testDayCount() {

		assertEquals(
				ChartDateUtil.getDayCount(
				ChartDateUtil.getMillisFromDate(2017, 4, 9),
				ChartDateUtil.getMillisFromDate(2017, 4, 10), true
				),
				2
		);
		assertEquals(
				ChartDateUtil.getDayCount(
				ChartDateUtil.getMillisFromDate(2017, 4, 9),
						ChartDateUtil.getMillisFromDate(2017, 4, 10), false
				),
				1
		);
		assertEquals(
				ChartDateUtil.getDayCount(
				ChartDateUtil.getMillisFromDate(2016, 4, 10),
						ChartDateUtil.getMillisFromDate(2017, 4, 10), true
				),
				366
		);
		assertNotEquals(
				ChartDateUtil.getDayCount(
				ChartDateUtil.getMillisFromDate(2016, 4, 10),
						ChartDateUtil.getMillisFromDate(2017, 4, 10), true
				),
				364
		);
		assertEquals(
				ChartDateUtil.getDayCount(
				ChartDateUtil.getMillisFromDate(2013, 10, 11),
						ChartDateUtil.getMillisFromDate(2017, 4, 10), true
				),
				1278
		);

	}
}
