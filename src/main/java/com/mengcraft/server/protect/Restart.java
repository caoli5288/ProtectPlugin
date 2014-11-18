package com.mengcraft.server.protect;

import org.bukkit.Bukkit;

public class Restart implements Runnable {
	@Override
	public void run() {
		if (Bukkit.getOnlinePlayers().length < 1) {
			Bukkit.shutdown();
		}
	}
}
