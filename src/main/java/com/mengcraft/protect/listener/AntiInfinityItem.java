package com.mengcraft.protect.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AntiInfinityItem implements Listener {

	private final static ItemStack AIR = new ItemStack(Material.AIR);

	@EventHandler
	public void handle(InventoryClickEvent event) {
		if (event.getClick() != ClickType.NUMBER_KEY) {
			ItemStack stack = event.getCurrentItem();
			if (stack != null && stack.getAmount() < 0) {
				event.setCancelled(true);
				event.setCurrentItem(AIR);
			}
		} else {
			int i = event.getHotbarButton();
			ItemStack stack = event.getWhoClicked().getInventory().getItem(i);
			if (stack != null && stack.getAmount() < 0) {
				event.setCancelled(true);
				event.getWhoClicked().getInventory().setItem(i, AIR);
			}
		}
	}

	@EventHandler
	public void handle(ItemSpawnEvent event) {
		if (event.getEntity().getItemStack().getAmount() < 0) {
			event.setCancelled(true);
		}
	}

}
