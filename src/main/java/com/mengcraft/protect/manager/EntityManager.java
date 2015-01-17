package com.mengcraft.protect.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class EntityManager {

	private final static EntityManager MANAGER = new EntityManager();

	public Map<String, Integer> getEntityInfo(World world) {
		Map<String, Integer> map = new HashMap<>();
		for (Entity entity : world.getEntities()) {
			addCount(map, entity.getType().name());
		}
		return map;
	}

	private void addCount(Map<String, Integer> map, String name) {
		if (map.containsKey(name)) {
			map.put(name, map.get(name) + 1);
		} else {
			map.put(name, 1);
		}
	}

	public Map<String, Integer> getEntityInfo(List<World> worlds) {
		Map<String, Integer> map = new HashMap<>();
		for (World world : worlds) {
			addCount(map, getEntityInfo(world));
		}
		return map;
	}

	private void addCount(Map<String, Integer> map, Map<String, Integer> info) {
		for (Entry<String, Integer> entry : info.entrySet()) {
			String key = entry.getKey();
			if (map.containsKey(key)) {
				map.put(key, map.get(key) + entry.getValue());
			} else {
				map.put(key, entry.getValue());
			}
		}
	}

	public int purgeEntity(List<World> worlds, String type, int limit) {
		int count = 0;
		for (World world : worlds) {
			count += purgeEntity(world, type, limit);
		}
		return count;
	}

	public int purgeEntity(World world, String type, int limit) {
		int count = 0;
		for (Entity entity : world.getEntities()) {
			if (entity instanceof Player) {
				// Do not purge any player!
			} else if (type.equals("ALL")) {
				count += checkPurge(entity, limit);
			} else if (entity.getType().name().equals(type)) {
				count += checkPurge(entity, limit);
			}
		}
		return count;
	}

	private int checkPurge(Entity entity, int limit) {
		if (check(entity, limit) > 0) {
			entity.remove();
			return 1;
		}
		return 0;
	}

	public int check(Entity entity, int limit) {
		List<Entity> entities = entity.getNearbyEntities(16, 16, 16);
		CheckBox box = new CheckBox(entity.getType().name());
		for (Entity e : entities) {
			box.check(e);
		}
		if (box.getResult() > limit) {
			return 1;
		}
		return 0;
	}

	public static EntityManager getManager() {
		return MANAGER;
	}

	public Events getEvents(int limit) {
		return new Events(limit);
	}

	private class Events implements Listener {

		private final int limit;

		public Events(int limit) {
			this.limit = limit;
		}

		@EventHandler
		public void onCreatureSpawn(CreatureSpawnEvent event) {
			int i = check(event.getEntity(), this.limit);
			event.setCancelled(i > 0);
		}

	}

	private class CheckBox {

		private final String type;
		private int result = 0;

		public void check(Entity entity) {
			if (entity.getType().name().equals(this.type)) {
				this.result++;
			}
		}

		public CheckBox(String name) {
			this.type = name;
		}

		public int getResult() {
			return this.result;
		}

	}
}
