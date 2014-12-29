package com.mengcraft.bukkit.protect.util;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilTest {
	@Test
	public void test() {
		TimeUtil util = new TimeUtil(System.currentTimeMillis());
		Assert.assertTrue(util.getDay() > 0);
	}
}
