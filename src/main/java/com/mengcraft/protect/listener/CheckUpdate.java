package com.mengcraft.protect.listener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CheckUpdate implements Runnable {

	private final CommandSender sender;
	private final InputStream local;

	@Override
	public void run() {
		sender.sendMessage(ChatColor.GREEN + "开始检查更新...");
		try {
			InputStreamReader reader = new InputStreamReader(this.local, "UTF-8");
			char[] buffer = new char[256];
			reader.read(buffer);
			String cache = new String(buffer);
			sender.sendMessage(ChatColor.GREEN + "本地版本序列: " + cache);
			// This file created by jenkins.
			URL url = new URL("http://ci.mengcraft.com:8080/job/ProtectPlugin/ws/src/main/resources/checkuuid");
			new InputStreamReader(url.openStream(), "UTF-8").read(buffer);
			String remote = new String(buffer);
			sender.sendMessage(ChatColor.GREEN + "远程版本序列: " + remote);

			if (cache.equals(remote)) {
				sender.sendMessage(ChatColor.GREEN + "你使用的ProtectPlugin是最新版！");
			} else {
				sender.sendMessage(ChatColor.GREEN + "你使用的ProtectPlugin不是最新版！");
				sender.sendMessage(ChatColor.GREEN + "当前版本不支持自动更新请手动下载更新！");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public CheckUpdate(CommandSender sender, InputStream local) {
		this.sender = sender;
		this.local = local;
	}

}
