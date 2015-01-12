package com.mengcraft.bukkit.protect.util;

import org.junit.Assert;
import org.junit.Test;

import com.mengcraft.protect.util.TimeUtils;

public class TimeUtilTest {
	@Test
	public void test() {
		TimeUtils util = new TimeUtils(System.currentTimeMillis());
		Assert.assertTrue(util.getDay() > 0);
	}
}
