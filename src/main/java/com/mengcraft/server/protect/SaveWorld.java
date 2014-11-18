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
}
