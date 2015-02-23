package com.mengcraft.protect.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class AntiCopyItem implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void handle(BlockBreakEvent event) {
		BlockState state = event.getBlock().getState();
		if (state instanceof Chest) {
			Chest chest = (Chest) state;
			if (check(chest) > 0) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "你无法破坏正在被使用的箱子");
			}
		}
	}

	private int check(Chest state) {
		return state.getBlockInventory().getViewers().size();
	}

}
