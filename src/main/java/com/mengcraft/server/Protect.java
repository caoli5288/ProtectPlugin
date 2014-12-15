package com.mengcraft.server;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.defaults.SaveOffCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.mengcraft.server.protect.AntiRedClock;
import com.mengcraft.server.protect.CheckDisk;
import com.mengcraft.server.protect.Commands;
import com.mengcraft.server.protect.AntiExplosion;
import com.mengcraft.server.protect.AntiJoinBot;
import com.mengcraft.server.protect.KeepFarm;
import com.mengcraft.server.protect.KickFull;
import com.mengcraft.server.protect.ModifySpigot;
import com.mengcraft.server.protect.ReBirth;
import com.mengcraft.server.protect.Restart;
import com.mengcraft.server.protect.SaveWorld;
import com.mengcraft.server.protect.SpawnMob;
import com.mengcraft.server.protect.UnloadChunk;

public class Protect extends JavaPlugin {
	private static Protect instance;

	@Override
	public void onLoad() {
		setInstance(this);
		saveDefaultConfig();
	}

	@Override
	public void onEnable() {
		getCommand("protect").setExecutor(new Commands());

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
			Bukkit.getScheduler().runTaskTimer(get(), new Restart(limit), delay, 6000);
			getLogger().info("智能重启服务器已开启");
		}
		if (getConfig().getBoolean("keepfarm.use", true)) {
			Bukkit.getPluginManager().registerEvents(new KeepFarm(), get());
			getLogger().info("防止耕地被破坏已开启");
		}
		if (getConfig().getBoolean("joinbot.use", true)) {
			long delay = getConfig().getLong("joinbot.value", 60) * 20;
			getServer().getScheduler().runTaskTimer(get(), new AntiJoinBot(), delay, delay);
			getLogger().info("防止爆服器爆服已开启");
		}
		if (getConfig().getBoolean("saveworld.use", true)) {
			long delay = getConfig().getLong("saveworld.value", 60) * 20;
			new SaveOffCommand().execute(getServer().getConsoleSender(), null, null);
			getServer().getScheduler().runTaskTimer(get(), new SaveWorld(), delay, delay);
			getLogger().info("流畅的保存地图已开启");
		}
		if (getConfig().getBoolean("spawnmob.use", true)) {
			int value = getConfig().getInt("spawnmob.value", 16);
			Bukkit.getPluginManager().registerEvents(new SpawnMob(value), get());
			getLogger().info("防止密集养殖场已开启");
		}
		if (getConfig().getBoolean("unchunk.use", true)) {
			long value = getConfig().getLong("unchunk.value", 10) * 1200;
			Bukkit.getScheduler().runTaskTimer(this, UnloadChunk.getUnloadChunk(), value, value);
			Bukkit.getPluginManager().registerEvents(UnloadChunk.getUnloadChunk(), this);
			getLogger().info("防止区块卡太多已开启");
		}
		if (getConfig().getBoolean("spigot.use", true)) {
			int value = getConfig().getInt("spigot.value", 4);
			Bukkit.getScheduler().runTask(get(), new ModifySpigot(value));
			getLogger().info("优化Spigot配置已开启");
		}
		if (getConfig().getBoolean("explosion.use", true)) {
			Bukkit.getPluginManager().registerEvents(new AntiExplosion(), this);
			getLogger().info("防止爆炸毁地图已开启");
		}

		Bukkit.getPluginManager().registerEvents(new KickFull(), get());
		Bukkit.getPluginManager().registerEvents(CheckDisk.getCheckFree(), this);
		Bukkit.getScheduler().runTaskTimer(this, CheckDisk.getCheckFree(), 0, 18000);
		getServer().getScheduler().runTaskTimer(getServer().getPluginManager().getPlugins()[0], new ReBirth(), 100, 100);

		getLogger().info("欢迎使用梦梦家服务器");
		getLogger().info("http://www.mengcraft.com");
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
