package com.mengcraft.server;

import java.io.IOException;

import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.mengcraft.server.protect.Events;

public class Protect extends JavaPlugin {
	private static Protect instance;

	@Override
	public void onLoad() {
		setInstance(this);
	}

	@Override
	public void onEnable() {
		Events e = new Events();
		getServer().getPluginManager().registerEvents(e, get());
		getServer().getScheduler().runTaskTimer(get(), e, 1200, 1200);
		getServer().getScheduler().runTaskTimer(getServer().getPluginManager().getPlugins()[0], new ReBirth(), 100, 100);
		getLogger().info("你的服务器已经被保护");
		getLogger().info("欢迎使用梦梦家服务器");
		try {
			new Metrics(get()).start();
		} catch (IOException e1) {
			getLogger().warning("Cant link to mcstats.org!");
		}
	}

	public static Protect get() {
		return instance;
	}

	private static void setInstance(Protect instance) {
		Protect.instance = instance;
	}
}
