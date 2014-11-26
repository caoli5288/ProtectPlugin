package com.mengcraft.server.protect;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class CheckFree implements Runnable {
	private final static CheckFree CPDB = new CheckFree();
	private final String cpMsg = ChatColor.RED + "CoreProtect超过1GB请及时清理";
	private final String freeMsg = ChatColor.RED + "硬盘总空间已经不足1GB请及时清理";
	private boolean cp = false;
	private boolean free = false;

	private CheckFree() {
	}

	@Override
	public void run() {
		if (Bukkit.getPluginManager().getPlugin("CoreProtect") != null) {
			Plugin cp = Bukkit.getPluginManager().getPlugin("CoreProtect");
			File db = new File(cp.getDataFolder(), "database.db");
			if (db.exists() && db.length() > 1073741824) {
				this.cp = true;
			} else {
				this.cp = false;
			}
		}
		if (new File(".").getFreeSpace() < 1073741824) {
			this.free = true;
		} else {
			this.free = false;
		}
	}
	
	public boolean getFreeStatus() {
		return this.free;
	}
	
	public String getFreeMessage() {
		return this.freeMsg;
	}

	public boolean getCPStatus() {
		return this.cp;
	}

	public String getCPMessage() {
		return this.cpMsg;
	}

	public static CheckFree get() {
		return CPDB;
	}
}
