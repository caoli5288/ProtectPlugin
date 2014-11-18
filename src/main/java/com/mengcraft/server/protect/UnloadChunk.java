package com.mengcraft.server.protect;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

public class UnloadChunk implements Runnable{
	@Override
	public void run() {
		for (World world : Bukkit.getWorlds()) {
			for (Chunk chunk : world.getLoadedChunks()) {
				chunk.unload(true, true);
			}
		}
	}
}
