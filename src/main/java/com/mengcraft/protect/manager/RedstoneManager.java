package com.mengcraft.protect.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockRedstoneEvent;

public class RedstoneManager {
	
	private final static RedstoneManager MANAGER = new RedstoneManager(); 

	private final Map<Block, Integer> map = new HashMap<>();

	private class Task implements Runnable {

		private final List<Block> blocks = new ArrayList<>();
		private final int limit;
		private final Map<Block, Integer> map;

		public Task(Map<Block, Integer> map, int limit) {
			this.map = map;
			this.limit = limit;
		}

		@Override
		public void run() {
			for (Entry<Block, Integer> entry : this.map.entrySet()) {
				checkAdd(entry);
			}
			for (Block block : this.blocks) {
				checkBreak(block);
			}
			this.map.clear();
			this.blocks.clear();
		}

		private void checkAdd(Entry<Block, Integer> entry) {
			if (entry.getValue() > this.limit) {
				this.blocks.add(entry.getKey());
			}
		}

		private void checkBreak(Block block) {
			if (!block.getType().name().contains("SIGN")) {
				block.breakNaturally();
			}
		}

	}

	private class Events implements Listener {

		private final Map<Block, Integer> map;

		public Events(Map<Block, Integer> map) {
			this.map = map;
		}
		
		@EventHandler
		public void onRedClock(BlockRedstoneEvent event) {
			put(event.getBlock());
		}

		private void put(Block block) {
			if (this.map.containsKey(block)) {
				this.map.put(block, this.map.get(block) + 1);
			} else {
				this.map.put(block, 1);
			}
		}

	}

	public Events getEvents() {
		return new Events(this.map);
	}

	public Task getTask(int limit) {
		return new Task(this.map, limit);
	}

	public static RedstoneManager getManager() {
		return MANAGER;
	}

}
