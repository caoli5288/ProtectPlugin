package com.mengcraft.server.protect;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;

public class ReBirth implements Runnable {
	@Override
	public void run() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ProtectPlugin");
		if (plugin != null) {
			if (!plugin.isEnabled()) {
				Bukkit.getServer().getPluginManager().enablePlugin(plugin);
			}
		} else {
			try {
				File file = new File("plugins/ProtectPlugin.jar");
				Bukkit.getServer().getPluginManager().loadPlugin(file);
			} catch (UnknownDependencyException e) {
				e.printStackTrace();
			} catch (InvalidPluginException e) {
				e.printStackTrace();
			} catch (InvalidDescriptionException e) {
				e.printStackTrace();
			}
		}
	}
}
