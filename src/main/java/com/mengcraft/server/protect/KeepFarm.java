package com.mengcraft.server.protect;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import com.mengcraft.server.Protect;

public class KeepFarm implements Listener {

	private int x;

	public KeepFarm() {
		if (Bukkit.getPluginManager().getPlugin("FarmProtect") != null) {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("FarmProtect");
			if (plugin.isEnabled()) {
				Bukkit.getPluginManager().disablePlugin(plugin);
				Protect.get().getLogger().info("禁用FarmProtect插件成功");
			} else {
				setX(Bukkit.getScheduler().runTaskTimer(Protect.get(), new Killer(), 120, 120).getTaskId());
			}
		}
	}

	@EventHandler
	public void soilChangePlayer(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.PHYSICAL) && event.getClickedBlock().getType().equals(Material.SOIL)) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void soilChangeEntity(EntityInteractEvent event) {
		if (event.getEntityType() != EntityType.PLAYER && event.getBlock().getType().equals(Material.SOIL)) {
			event.setCancelled(true);
		}
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	private class Killer implements Runnable {
		@Override
		public void run() {
			Plugin plugin = Bukkit.getPluginManager().getPlugin("FarmProtect");
			if (plugin.isEnabled()) {
				Bukkit.getPluginManager().disablePlugin(plugin);
				Protect.get().getLogger().info("禁用FarmProtect插件成功");
				Bukkit.getScheduler().cancelTask(getX());
			}
		}
	}
}
