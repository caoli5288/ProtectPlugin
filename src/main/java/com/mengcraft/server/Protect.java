package com.mengcraft.server;

import java.io.IOException;

import org.bukkit.command.defaults.SaveOffCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.mengcraft.server.protect.Events;
import com.mengcraft.server.protect.ReBirth;

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
		new SaveOffCommand().execute(getServer().getConsoleSender(), null, null);

		if (getServer().getPluginManager().getPlugin("AutoSave") != null) {
			Plugin plugin = getServer().getPluginManager().getPlugin("AutoSave");
			getServer().getPluginManager().disablePlugin(plugin);
			getLogger().info("已禁用AutoSave插件");
		}
		if (getServer().getPluginManager().getPlugin("AutoSaveWorld") != null) {
			Plugin plugin = getServer().getPluginManager().getPlugin("AutoSaveWorld");
			getServer().getPluginManager().disablePlugin(plugin);
			getLogger().info("已禁用AutoSaveWorld插件");
		}
		if (getServer().getPluginManager().getPlugin("FarmProtect") != null) {
			Plugin plugin = getServer().getPluginManager().getPlugin("FarmProtect");
			getServer().getPluginManager().disablePlugin(plugin);
			getLogger().info("已禁用FarmProtect插件");
		}
		
		getLogger().info("流畅的保存地图已开启");
		getLogger().info("防止爆服器爆服已开启");
		getLogger().info("防止服务器过载已开启");
		getLogger().info("防止密集养殖场已开启");
		getLogger().info("防止耕地被破坏已开启");
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
