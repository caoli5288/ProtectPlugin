package com.mengcraft.server.protect;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class CheckDisk implements Runnable, Listener {
	private final static CheckDisk CHECK_FREE = new CheckDisk();
	private final static String COREPROTEC_MESSAGE = ChatColor.RED + "CoreProtect超过1GB请及时清理";
	private final static String FREE_SPACE_MESSAGE = ChatColor.RED + "硬盘总空间已经不足1GB请及时清理";
	private boolean a = false;
	private boolean b = false;

	private CheckDisk() {
	}
	
	@EventHandler
	public void onOpJoin(PlayerJoinEvent event) {
		if (event.getPlayer().isOp()) {
			if (this.a) {
				event.getPlayer().sendMessage(COREPROTEC_MESSAGE);
			}
			if (this.b) {
				event.getPlayer().sendMessage(FREE_SPACE_MESSAGE);
			}
		}
	}
	
	@Override
	public void run() {
		if (Bukkit.getPluginManager().getPlugin("CoreProtect") != null) {
			Plugin cp = Bukkit.getPluginManager().getPlugin("CoreProtect");
			File db = new File(cp.getDataFolder(), "database.db");
			if (db.exists() && db.length() > 1073741824) {
				this.a = true;
			} else {
				this.a = false;
			}
		}
		if (new File(".").getFreeSpace() < 1073741824) {
			this.b = true;
		} else {
			this.b = false;
		}
	}

	public static CheckDisk getCheckFree() {
		return CHECK_FREE;
	}
}
