package com.mengcraft.server;

import java.io.IOException;

import org.bukkit.command.defaults.SaveOffCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.mengcraft.server.protect.Events;
import com.mengcraft.server.protect.ReBirth;
import com.mengcraft.server.protect.SaveWorld;

public class Protect extends JavaPlugin {
	private static Protect instance;

	@Override
	public void onLoad() {
		setInstance(this);
	}

	@Override
	public void onEnable() {
		getServer().getScheduler().runTaskTimer(get(), new Events(), 1200, 1200);
		getServer().getScheduler().runTaskTimer(getServer().getPluginManager().getPlugins()[0], new ReBirth(), 100, 100);
		getServer().getScheduler().runTaskTimer(get(), new SaveWorld(), 1200, 1200);
		getServer().getScheduler().runTaskLater(get(), new Killer(), 600);

		new SaveOffCommand().execute(getServer().getConsoleSender(), null, null);

		getLogger().info("流畅的保存地图已开启");
		getLogger().info("防止区块卡太多已开启");
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

	private class Killer implements Runnable {
		@Override
		public void run() {
			if (getServer().getPluginManager().getPlugin("AutoSave") != null) {
				Plugin plugin = getServer().getPluginManager().getPlugin("AutoSave");
				if (plugin.isEnabled()) {
					getServer().getPluginManager().disablePlugin(plugin);
					getLogger().info("已禁用AutoSave插件");
				}
			}
			if (getServer().getPluginManager().getPlugin("AutoSaveWorld") != null) {
				Plugin plugin = getServer().getPluginManager().getPlugin("AutoSaveWorld");
				if (plugin.isEnabled()) {
					getServer().getPluginManager().disablePlugin(plugin);
					getLogger().info("已禁用AutoSaveWorld插件");
				}
			}
			if (getServer().getPluginManager().getPlugin("FarmProtect") != null) {
				Plugin plugin = getServer().getPluginManager().getPlugin("FarmProtect");
				if (plugin.isEnabled()) {
					getServer().getPluginManager().disablePlugin(plugin);
					getLogger().info("已禁用FarmProtect插件");
				}
			}
		}
	}
}
