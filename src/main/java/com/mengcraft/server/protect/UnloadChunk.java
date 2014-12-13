package com.mengcraft.server.protect;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldInitEvent;

public class UnloadChunk implements Runnable, Listener {
	private final static UnloadChunk UNLOAD_CHUNK = new UnloadChunk();

	private UnloadChunk() {
		// Fix done
		for (World world : Bukkit.getWorlds()) {
			world.setKeepSpawnInMemory(false);
		}
	}

	public static UnloadChunk getUnloadChunk() {
		return UNLOAD_CHUNK;
	}

	@Override
	public void run() {
		int i = 0;
		int j = 0;
		for (World world : Bukkit.getWorlds()) {
			i = i + world.getLoadedChunks().length;
			for (Chunk chunk : world.getLoadedChunks()) {
				chunk.unload(true, true);
			}
			j = j + world.getLoadedChunks().length;
		}
		i = i - j;
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "Purge chunk number: " + i);
	}

	@EventHandler
	public void onWorldInit(WorldInitEvent event) {
		event.getWorld().setKeepSpawnInMemory(false);
	}

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event) {
		System.out.println("Protect.loadChunk.Fire");
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		System.out.println("Protect.UnloadChunk.Fire");
	}
}
