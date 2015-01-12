package com.mengcraft.protect.manager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class TickManager {
	private final static TickManager MANAGER = new TickManager();
	private final Task task = new Task();
	private final List<Double> tps = new ArrayList<>();
	private long last = System.currentTimeMillis();

	public TickManager() {
		tps.add(20.0);
	}

	private class Task implements Runnable {
		public void run() {
			BigDecimal tick = new BigDecimal("1200");
			BigDecimal per = new BigDecimal(System.currentTimeMillis() - getLast());
			BigDecimal second = per.divide(new BigDecimal("1000"), 4, RoundingMode.HALF_UP);
			double tps = tick.divide(second, 2, RoundingMode.HALF_UP).doubleValue();
			List<Double> recent = getTps();
			recent.add(0, tps > 20 ? 20 : tps);
			if (recent.size() > 3) {
				recent.remove(3);
			}
			setLast(System.currentTimeMillis());
		};
	}

	public static TickManager getManager() {
		return MANAGER;
	}

	public Task getTask() {
		return task;
	}

	public List<Double> getTps() {
		return tps;
	}

	private long getLast() {
		return last;
	}

	private void setLast(long last) {
		this.last = last;
	}
}
