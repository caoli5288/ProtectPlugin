package com.mengcraft.server.protect;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class SaveWorld implements Runnable {

	private int x;

	public SaveWorld() {
		setX(0);
	}

	@Override
	public void run() {
		List<World> worlds = Bukkit.getServer().getWorlds();
		if (getX() < worlds.size()) {
			worlds.get(getX()).save();
			Chunk[] chunks = worlds.get(getX()).getLoadedChunks();
			for (Chunk chunk : chunks) {
				chunk.unload(true, true);
			}
			setX(getX() + 1);
		} else {
			setX(0);
			run();
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}
}
