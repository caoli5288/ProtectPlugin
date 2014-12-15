package com.mengcraft.server.protect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class AntiRedClock implements Runnable, Listener {
	private final int limit;
	private final Map<Block, Integer> map = new HashMap<Block, Integer>();

	public AntiRedClock(int limit) {
		this.limit = limit;
	}

	@Override
	public void run() {
		List<Block> blocks = new ArrayList<>();
		for (Entry<Block, Integer> entry : this.map.entrySet()) {
			// System.out.println(entry.getValue());
			if (entry.getValue() > this.limit) {
				blocks.add(entry.getKey());
			}
		}
		for (Block block : blocks) {
			block.breakNaturally();
		}
		this.map.clear();
	}

	@EventHandler
	public void onRedClock(BlockRedstoneEvent event) {
		put(event.getBlock());
	}

	private void put(Block block) {
		if (this.map.containsKey(block)) {
			int i = this.map.remove(block);
			this.map.put(block, i + 1);
		} else {
			this.map.put(block, 1);
		}
	}
}
