package com.mengcraft.protect.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class AntiExplosion implements Listener {
	@EventHandler
	public void onExplosion(EntityExplodeEvent event) {
		event.blockList().clear();
	}
}
