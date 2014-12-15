package com.mengcraft.server.protect;

import org.bukkit.Bukkit;

public class Restart implements Runnable {

	private final int limit;

	public Restart(int limit) {
		this.limit = limit;
	}

	@Override
	public void run() {
		if (Bukkit.getOnlinePlayers().length <= this.limit) {
			Bukkit.shutdown();
		}
	}
}
