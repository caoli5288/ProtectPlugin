package com.mengcraft.protect.listener;

import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.inventory.InventoryHolder;

public class AntiCopyItem implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void handle(BlockBreakEvent event) {
		BlockState state = event.getBlock().getState();
		if (state instanceof InventoryHolder) {
			InventoryHolder chest = (InventoryHolder) state;
			if (check(chest) > 0) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + "你无法破坏正在被使用的箱子");
			}
		}
	}

	@EventHandler
	public void handle(EntityPortalEvent event) {
		String type = event.getEntity().getType().name();
		if (type.contains("MINECART")) {
			event.setCancelled(true);
		}
	}

	private int check(InventoryHolder state) {
		return state.getInventory().getViewers().size();
	}

}
