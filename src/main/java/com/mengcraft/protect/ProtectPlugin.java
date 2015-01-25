package com.mengcraft.protect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.Bukkit;
import org.bukkit.command.defaults.SaveOffCommand;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import com.mengcraft.protect.listener.AntiBreakFarm;
import com.mengcraft.protect.listener.AntiExplosion;
import com.mengcraft.protect.listener.AntiOverload;
import com.mengcraft.protect.listener.PlayerLogin;
import com.mengcraft.protect.manager.EntityManager;
import com.mengcraft.protect.manager.PlayerRecordManager;
import com.mengcraft.protect.manager.RedstoneManager;
import com.mengcraft.protect.manager.TickManager;
import com.mengcraft.protect.task.ModifySpigot;
import com.mengcraft.protect.task.ReBirth;
import com.mengcraft.protect.task.Restart;
import com.mengcraft.protect.task.SaveWorld;

public class ProtectPlugin extends JavaPlugin {

	private final PluginKiller killer = PluginKiller.getKiller();

	@Override
	public void onLoad() {
		saveDefaultConfig();
	}

	@Override
	public void onEnable() {
		if (getConfig().getBoolean("redclock.use", true)) {
			int limit = getConfig().getInt("redclock.value", 30);
			RedstoneManager manager = RedstoneManager.getManager();
			Bukkit.getPluginManager().registerEvents(manager.getEvents(), this);
			Bukkit.getScheduler().runTaskTimer(this, manager.getTask(limit), 20, 20);
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
			getKiller().addName("FarmProtect");
			getLogger().info("防止耕地被破坏已开启");
		}
		if (getConfig().getBoolean("saveworld.use", true)) {
			long delay = getConfig().getLong("saveworld.value", 60) * 20;
			new SaveOffCommand().execute(getServer().getConsoleSender(), null, null);
			getServer().getScheduler().runTaskTimer(this, new SaveWorld(this), delay, delay);
			getKiller().addName("AutoSaveWorld");
			getKiller().addName("AutoSave");
			getKiller().addName("NoSpawnChunks");
			getLogger().info("流畅的保存地图已开启");
		}
		if (getConfig().getBoolean("spawnmob.use", true)) {
			int value = getConfig().getInt("spawnmob.value", 16);
			EntityManager manager = EntityManager.getManager();
			Bukkit.getPluginManager().registerEvents(manager.getEvents(value), this);
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
		if (getConfig().getBoolean("sendpacket.use", true)) {
			enableAntiSendPacket();
		}
		Bukkit.getPluginManager().registerEvents(new AntiOverload(this), this);
		Bukkit.getPluginManager().registerEvents(getKiller().getEvents(), this);
		getServer().getPluginManager().registerEvents(new PlayerLogin(), this);
		Bukkit.getScheduler().runTaskTimer(this, TickManager.getManager().getTask(), 1200, 1200);
		Bukkit.getScheduler().runTaskTimer(getServer().getPluginManager().getPlugins()[0], new ReBirth(), 100, 100);
		getLogger().info("监控硬盘空间等已开启");
		getLogger().info("梦梦家服务器官网地址");
		getLogger().info("http://www.mengcraft.com");
		getCommand("protect").setExecutor(new Commands(this));
		try {
			new Metrics(this).start();
		} catch (IOException e) {
			getLogger().warning("Cant link to mcstats.org!");
		}
		getKiller().runPluginKiller();
	}

	private void enableAntiSendPacket() {
		String name = getServer().getClass().getName();
		if (name.startsWith("org.bukkit.craftbukkit.v1_7_R1")) {
			File file = getResourceFile("injector-1.7.2.module");
			enablePlugin(file);
			getLogger().info("防发包测压蹦服已开启");
		} else if (name.startsWith("org.bukkit.craftbukkit.v1_7_R4")) {
			File file = getResourceFile("injector-1.7.10.module");
			enablePlugin(file);
			getLogger().info("防发包测压蹦服已开启");
		} else {
			getLogger().info("没有适合版本的防发包");
		}
	}

	private void enablePlugin(File file) {
		try {
			Plugin plugin = getServer().getPluginManager().loadPlugin(file);
			getServer().getPluginManager().enablePlugin(plugin);
		} catch (UnknownDependencyException e) {
			e.printStackTrace();
		} catch (InvalidPluginException e) {
			e.printStackTrace();
		} catch (InvalidDescriptionException e) {
			e.printStackTrace();
		}
	}

	private File getResourceFile(String name) {
		File output = new File(getDataFolder(), "_temp_.jar");
		InputStream in = getResource(name);
		byte[] buffer = new byte[8192];
		try {
			FileOutputStream out = new FileOutputStream(output);
			for (int i = in.read(buffer); i > 0; i = in.read(buffer)) {
				out.write(buffer, 0, i);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		output.deleteOnExit();
		return output;
	}

	@Override
	public void onDisable() {
		PlayerRecordManager.getManager().saveRecords();
	}

	public PluginKiller getKiller() {
		return killer;
	}
}
