package com.mengcraft.server.protect;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import com.mengcraft.server.Protect;

public class SaveWorld implements Runnable {

	private int x;
	private int y;
	private int z;

	public SaveWorld() {
		setX(0);
		if (Bukkit.getPluginManager().getPlugin("AutoSave") != null) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("AutoSave");
			if (plugin.isEnabled()) {
				Bukkit.getPluginManager().disablePlugin(plugin);
				Protect.get().getLogger().info("禁用AutoSave插件成功");
			} else {
				setY(Bukkit.getScheduler().runTaskTimer(Protect.get(), new KillAutoSave(), 120, 120).getTaskId());
			}
		}
		if (Bukkit.getPluginManager().getPlugin("AutoSaveWorld") != null) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("AutoSaveWorld");
			if (plugin.isEnabled()) {
				Bukkit.getPluginManager().disablePlugin(plugin);
				Protect.get().getLogger().info("禁用AutoSaveWorld插件成功");
			} else {
				setZ(Bukkit.getScheduler().runTaskTimer(Protect.get(), new KillAutoSaveWorld(), 120, 120).getTaskId());
			}
		}
	}

	/**
	 * unload chunks must be in main thread!
	 */
	@Override
	public void run() {
		List<World> worlds = Bukkit.getWorlds();
		if (getX() >= worlds.size()) {
			setX(0);
		}
		Chunk[] chunks = worlds.get(getX()).getLoadedChunks();
		for (Chunk chunk : chunks) {
			chunk.unload(true, true);
		}
		new Thread(new SaveWorldTask(getX())).start();
		setX(getX() + 1);
	}

	private int getX() {
		return x;
	}

	private void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}

	private class SaveWorldTask implements Runnable {
		private final int count;

		public SaveWorldTask(int x) {
			this.count = x;
		}

		@Override
		public void run() {
			Bukkit.getWorlds().get(getCount()).save();
			if (getCount() < 1) {
				Bukkit.savePlayers();
			}
		}

		public int getCount() {
			return count;
		}
	}

	private class KillAutoSave implements Runnable {
		@Override
		public void run() {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("AutoSave");
			if (plugin.isEnabled()) {
				Bukkit.getPluginManager().disablePlugin(plugin);
				Protect.get().getLogger().info("禁用AutoSave插件成功");
				Bukkit.getScheduler().cancelTask(getY());
			}
		}
	}

	private class KillAutoSaveWorld implements Runnable {
		@Override
		public void run() {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("AutoSaveWorld");
			if (plugin.isEnabled()) {
				Bukkit.getPluginManager().disablePlugin(plugin);
				Protect.get().getLogger().info("禁用AutoSaveWorld插件成功");
				Bukkit.getScheduler().cancelTask(getZ());
			}
		}
	}
}
