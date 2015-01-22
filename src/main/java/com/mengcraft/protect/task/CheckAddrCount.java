package com.mengcraft.protect.task;

import java.util.Map.Entry;

import org.bukkit.command.defaults.BanIpCommand;
import org.bukkit.plugin.Plugin;

import com.mengcraft.protect.manager.DataHandler;
import com.mengcraft.protect.util.StringMap;

public class CheckAddrCount implements Runnable {

	private final StringMap<Integer> map = DataHandler.getHandler().getAddrCount();
	private final Plugin plugin;

	// Check every 2 second.
	@Override
	public void run() {
		StringMap<Integer> map = this.map;
		for (Entry<String, Integer> entry : map.entrySet()) {
			checkBan(entry);
		}
		map.clear();
	}

	private void checkBan(Entry<String, Integer> entry) {
		if (entry.getValue() > 8) {
			ban(entry.getKey());
		}
	}

	private void ban(String... key) {
		new BanIpCommand().execute(this.plugin.getServer().getConsoleSender(), null, key);
		this.plugin.getLogger().warning("检测 " + key[0] + " 过多登陆请求而封禁");
	}

	public CheckAddrCount(Plugin plugin) {
		this.plugin = plugin;
	}

}
