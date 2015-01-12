package com.mengcraft.protect.util;

public class TimeUtil {
	public final static long TIME_30_DAY = 2592000000L;
	public final static long TIME_DAY = 86400000L;
	public final static long TIME_HOUR = 3600000L;

	private final long time;
	private final long day;
	private final long hour;

	public TimeUtil(long in) {
		this.time = in;
		this.day = in / TIME_DAY;
		this.hour = in % TIME_DAY / TIME_HOUR;
	}

	public long getTime() {
		return time;
	}

	public long getDay() {
		return day;
	}

	public long getHour() {
		return hour;
	}
}
