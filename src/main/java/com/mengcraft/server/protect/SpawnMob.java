package com.mengcraft.server.protect;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SpawnMob implements Listener {
	private final int i;

	public SpawnMob(int count) {
		this.i = count;
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		List<Entity> entities = event.getEntity().getNearbyEntities(16, 16, 16);
		int count = 0;
		for (Entity entity : entities) {
			if (entity.getType().equals(event.getEntity().getType())) {
				count = count + 1;
			}
		}
		if (count > getLimit()) {
			event.setCancelled(true);
			// System.out.println("Events.OnCreatureSpawn.Cancelled");
		}
	}

	public int getLimit() {
		return i;
	}
}
