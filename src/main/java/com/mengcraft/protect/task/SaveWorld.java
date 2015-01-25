package com.mengcraft.protect.task;

import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public class SaveWorld implements Runnable {

	private int x = 0;

	private final Plugin plugin;

	/**
	 * unload chunks must be in main thread!
	 */
	@Override
	public void run() {
		List<World> worlds = this.plugin.getServer().getWorlds();
		if (worlds.size() <= this.x) {
			this.x = 0;
		}
		World world = worlds.get(this.x);
		Chunk[] chunks = world.getLoadedChunks();
		for (Chunk chunk : chunks) {
			chunk.unload(true, true);
		}
		world.save();
		this.x += 1;
	}

	public SaveWorld(Plugin plugin) {
		this.plugin = plugin;
	}

}
