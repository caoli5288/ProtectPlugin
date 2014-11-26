package com.mengcraft.server.protect;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinWarn implements Listener {
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isOp()) {
			if (CheckFree.get().getCPStatus()) {
				event.getPlayer().sendMessage(CheckFree.get().getCPMessage());
			}
			if (CheckFree.get().getFreeStatus()) {
				event.getPlayer().sendMessage(CheckFree.get().getFreeMessage());
			}
		}
	}
}
