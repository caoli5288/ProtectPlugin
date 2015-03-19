package com.mengcraft.protect.task;

import org.bukkit.Server;

public class Restart implements Runnable {

	private final int limit;
	private final Server server;

	public Restart(Server server, int limit) {
		this.server = server;
		this.limit = limit;
	}

	@Override
	public void run() {
		if (server.getOnlinePlayers().length <= this.limit) {
			server.dispatchCommand(server.getConsoleSender(), "save-all");
			server.shutdown();
		}
	}
}
