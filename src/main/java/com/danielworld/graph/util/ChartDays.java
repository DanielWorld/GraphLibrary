package com.danielworld.graph.util;

/**
 * enum Days
 * <br><br>
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-09.
 */

public enum ChartDays {
	Sunday(1), Monday(2), Tuesday(3), Wednesday(4), Thursday(5), Friday(6), Saturday(7);

	public final int value;

	ChartDays(int value) {
		this.value = value;
	}

	private int getNumber() {
		return this.value;
	}

	public static ChartDays getDate(int value) {
		for (ChartDays i : ChartDays.values()) {
			if (i.getNumber() == value)
				return i;
		}
		return ChartDays.Sunday;
	}
}
