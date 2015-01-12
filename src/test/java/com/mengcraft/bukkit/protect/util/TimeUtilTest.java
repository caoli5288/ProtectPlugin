package com.mengcraft.bukkit.protect.util;

import org.junit.Assert;
import org.junit.Test;

import com.mengcraft.protect.util.TimeUtil;

public class TimeUtilTest {
	@Test
	public void test() {
		TimeUtil util = new TimeUtil(System.currentTimeMillis());
		Assert.assertTrue(util.getDay() > 0);
	}
}
