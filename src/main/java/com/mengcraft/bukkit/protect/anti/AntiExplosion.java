package com.mengcraft.bukkit.protect.anti;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class AntiExplosion implements Listener {
	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		event.blockList().clear();
	}
}
