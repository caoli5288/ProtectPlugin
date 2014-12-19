package com.mengcraft.server;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.defaults.SaveOffCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.mengcraft.server.protect.AntiBreakFarm;
import com.mengcraft.server.protect.AntiExplosion;
import com.mengcraft.server.protect.AntiJoinBot;
import com.mengcraft.server.protect.AntiOverload;
import com.mengcraft.server.protect.AntiRedClock;
import com.mengcraft.server.protect.BannedSegmentManager;
import com.mengcraft.server.protect.CheckDisk;
import com.mengcraft.server.protect.Commands;
import com.mengcraft.server.protect.ModifySpigot;
import com.mengcraft.server.protect.PluginKiller;
import com.mengcraft.server.protect.ReBirth;
import com.mengcraft.server.protect.Restart;
import com.mengcraft.server.protect.SaveWorld;
import com.mengcraft.server.protect.AntiMobFarm;

public class Protect extends JavaPlugin {

	@Override
	public void onLoad() {
		saveDefaultConfig();
	}

	@Override
	public void onEnable() {
		if (getConfig().getBoolean("redclock.use", true)) {
			int limit = getConfig().getInt("redclock.value", 25);
			AntiRedClock red = new AntiRedClock(limit);
			Bukkit.getPluginManager().registerEvents(red, this);
			Bukkit.getScheduler().runTaskTimer(this, red, 20, 20);
			getLogger().info("防止超高频电路已开启");
		}
		if (getConfig().getBoolean("restart.use", true)) {
			long delay = getConfig().getLong("restart.value", 24) * 72000;
			int limit = getConfig().getInt("restart.limit", 0);
			Bukkit.getScheduler().runTaskTimer(this, new Restart(limit), delay, 6000);
			getLogger().info("智能重启服务器已开启");
		}
		if (getConfig().getBoolean("keepfarm.use", true)) {
			Bukkit.getPluginManager().registerEvents(new AntiBreakFarm(), this);
			PluginKiller.getKiller().addName("FarmProtect");
			getLogger().info("防止耕地被破坏已开启");
		}
		if (getConfig().getBoolean("joinbot.use", true)) {
			AntiJoinBot joinBot = new AntiJoinBot();
			long delay = getConfig().getLong("joinbot.value", 60) * 20;
			Bukkit.getScheduler().runTaskTimer(this, joinBot, delay, delay);
			Bukkit.getPluginManager().registerEvents(joinBot, this);
			getLogger().info("防止爆服器爆服已开启");
		}
		if (getConfig().getBoolean("saveworld.use", true)) {
			long delay = getConfig().getLong("saveworld.value", 60) * 20;
			new SaveOffCommand().execute(getServer().getConsoleSender(), null, null);
			getServer().getScheduler().runTaskTimer(this, new SaveWorld(), delay, delay);
			PluginKiller.getKiller().addName("AutoSaveWorld");
			PluginKiller.getKiller().addName("AutoSave");
			PluginKiller.getKiller().addName("NoSpawnChunks");
			getLogger().info("流畅的保存地图已开启");
		}
		if (getConfig().getBoolean("spawnmob.use", true)) {
			int value = getConfig().getInt("spawnmob.value", 16);
			Bukkit.getPluginManager().registerEvents(new AntiMobFarm(value), this);
			getLogger().info("防止密集养殖场已开启");
		}
		if (getConfig().getBoolean("spigot.use", true)) {
			int value = getConfig().getInt("spigot.value", 4);
			Bukkit.getScheduler().runTask(this, new ModifySpigot(value));
			getLogger().info("优化Spigot配置已开启");
		}
		if (getConfig().getBoolean("explosion.use", true)) {
			Bukkit.getPluginManager().registerEvents(new AntiExplosion(), this);
			getLogger().info("防止爆炸毁地图已开启");
		}
		if (true) {
			CheckDisk disk = new CheckDisk();
			Bukkit.getPluginManager().registerEvents(BannedSegmentManager.getManager().getEvents(), this);
			Bukkit.getPluginManager().registerEvents(disk, this);
			Bukkit.getPluginManager().registerEvents(new AntiOverload(), this);
			Bukkit.getPluginManager().registerEvents(PluginKiller.getKiller().getEvents(), this);
			Bukkit.getScheduler().runTaskTimer(getServer().getPluginManager().getPlugins()[0], new ReBirth(), 100, 100);
			Bukkit.getScheduler().runTaskTimer(this, disk, 0, 18000);
			getLogger().info("监控硬盘空间等已开启");
		}
		getLogger().info("欢迎使用梦梦家服务器");
		getLogger().info("http://www.mengcraft.com");
		getCommand("protect").setExecutor(new Commands());
		try {
			new Metrics(this).start();
		} catch (IOException e1) {
			getLogger().warning("Cant link to mcstats.org!");
		}
		PluginKiller.getKiller().runPluginKiller();
	}
}
