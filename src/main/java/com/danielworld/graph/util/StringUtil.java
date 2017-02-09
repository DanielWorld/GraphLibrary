package com.danielworld.graph.util;

/**
 * Copyright (C) 2014-2017 daniel@bapul.net
 * Created by Daniel on 2017-02-09.
 */

public class StringUtil {

	/**
	 If parameter String is null or length == 0 or length without empty space == 0 then return true
	 * @param str target String
	 * @return <b>true</b> : if target String is null or empty or blank
	 */
	public static boolean isNullorEmpty(String str){
		try {
			return (str == null || str.length() == 0 || str.trim().length() == 0);
		} catch (Exception e){
			return true;
		}
	}
}
